const Fields = ((element) => {
  const fieldName = $('#fieldName');
  const uploadFieldFile = $('#uploadFieldFile');
  const uploadFieldFileMenu = $('#uploadFieldFileMenu');

  function init() {
    initUI();
    // return
    getUserFields();
  }

  function initUI() {
    $('#fieldBorderColor').minicolors({
      control: 'hue',
      position: 'bottom left',
    });

    $('#fieldCrop').select2({
      placeholder: 'Select value',
      dropdownParent: '#saveFieldModal',
      allowClear: false
    });

    $('#fieldPlantingDate,#fieldHarvestDate').datepicker({
      autoclose: true,
      todayBtn: true,
      language: 'es',
      todayHighlight: true,
      format: 'dd/mm/yyyy',
      orientation: 'bottom right',
      container: '#saveFieldModal',
    });

    $('#fieldForm').submit(onFieldFormSubmit);
    $('#fmsCancelBtn').click(cancelSaveField);
    $('#drawNewFieldMenu').click(activateDrawField);
    $('#deleteFieldForm').submit(onDeleteFieldFormSubmit);

    $('#cancelFieldGeometry').click(onCancelFieldGeometry);
    $('#saveFieldGeometry').click(onSaveFieldGeometry);

    uploadFieldFileMenu.click(onUploadFieldFileMenuClick);
    uploadFieldFile.change(onUploadFieldFileChange);
  }

  function onUploadFieldFileMenuClick() {
    uploadFieldFile.click();
  }

  function onUploadFieldFileChange() {
    const files = uploadFieldFile[0].files;
    if (files && files.length > 0) {
      const file = uploadFieldFile[0].files[0];
      if (file && file.name.toLowerCase().endsWith('.zip')) {
        validateShapefile(file, (name, wkt) => {
          resetFieldModal();
          fieldName.val(name);
          $('#saveFieldModal').data('action', 'create-shapefile').data('wkt', wkt).modal('show');
        });
      } else {
        toastr.warning(`Seleccione un archivo con extensión ZIP que contenga los archivos del shapefile del lote.`);
      }
    }

    uploadFieldFile.val(null);
  }

  function validateShapefile(file, onSuccess) {
    file.arrayBuffer().then(arrayBuffer => {
      shp(arrayBuffer).then(geojson => {
        console.log(geojson);

        if (geojson.features > 1) {
          return toastr.warning(`Solo se permite un lote como geometría dentro de un archivo shapefile.`);
        }

        if (geojson.features > 1) {
          return toastr.warning(`Solo se permite un lote como geometría dentro de un archivo shapefile.`);
        }
        if (geojson.features == 0) {
          return toastr.warning(`El archivo shapefile está vacío.`);
        }

        const feature = geojson.features[0];
        if (feature.geometry.type !== 'Polygon') {
          return toastr.warning(`El tipo de geometría del archivo shapefile es inválida, solo se soportan Polígonos.`);
        }

        const poly = new ol.geom.Polygon(feature.geometry.coordinates);
        const wkt = OlMap.geometryToWKT(poly);

        onSuccess(geojson.fileName, wkt);
      }).catch(error => {
        console.log(error);
        toastr.warning(`Error al convertir shapefile en geojson`);
      });
    }).catch(error => {
      console.log(error);
      toastr.warning(`Error a la hora de leer el archivo shapefile`);
    });

  }

  element.filter = (filterText) => {
    $('#userFieldsList .field-list-item').filter(function () {
      $(this).toggle($(this).attr('fieldname').toLowerCase().indexOf(filterText) > -1);
    });
  };

  element.goToFieldInMap = (uuid) => {
    OlMap.goToFeature(uuid);
  }

  element.deleteField = (uuid, name) => {
    $('#deleteFieldName').text(name);
    $('#deleteFieldModal').data('uuid', uuid).modal('show');
  };

  element.editGeometry = (uuid) => {

    Indices.showIndices(false);
    CalendarImages.showCalendar(false);
    OlMapField.setVisibleFieldImage(false);

    OlMap.goToFeature(uuid);
    OlMap.activateModifyField(uuid);

    showEditGeometryTools(true);
  }

  element.editAttributes = (uuid) => {
    const field = OlMap.fieldByUuid(uuid);
    if (field) {
      resetFieldModal();

      fieldName.val(field.name);
      $('#fieldCrop').val(field.cropId).trigger('change.select2');
      $('#fieldBorderColor').minicolors('value', field.borderColor);
      $('#fieldBorderSize').val(field.borderSize);
      $('#fieldPlantingDate').datepicker('update', field.plantingDate);
      $('#fieldHarvestDate').datepicker('update', field.harvestDate);

      $('#saveFieldModal').data('action', 'update').data('uuid', uuid).modal('show');
    }
  }

  function showEditGeometryTools(visible) {
    if (visible) {
      $('#editFieldGeometryTools').hide().slideDown('fast');
      $('.disabled-on-edit-geometry').addClass('disabled');
    }
    else {
      $('#editFieldGeometryTools').show().slideUp('fast');
      $('.disabled-on-edit-geometry').removeClass('disabled');
    }
  }

  function onSaveFieldGeometry() {
    const field = OlMap.getModifiedField();
    if (field) {
      disableButton($('#cancelFieldGeometry'));
      disableButton($('#saveFieldGeometry'), true);

      $.post({
        method: 'PUT',
        url: `api/fields/${field.uuid}/geometry`,
        contentType: 'application/json'
      }, JSON.stringify(field)).done((field) => {
        showEditGeometryTools(false);

        OlMap.removeField(field.uuid);
        OlMap.addField(field);
        OlMap.cancelModifyField();

        OlMapField.removeFieldImage();
        OlMapField.setVisibleFieldImage(true);

        $(`#field-${field.uuid}`).replaceWith($(templateFieldUI(field)));

        toastr.success(`Los límites del lote han sido actualizados.`);
      }).fail((error) => {
        const code = error.responseJSON.code;
        if (code === 'field-not-found') {
          return toastr.warning(`El lote ya no existe, actualice su página.`);
        }
        toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
      }).always(() => {
        enableButton($('#saveFieldGeometry,#cancelFieldGeometry'));
      });
    }
  }

  function onCancelFieldGeometry() {
    OlMap.cancelModifyField();
    showEditGeometryTools(false);

    Indices.showIndices(true);
    CalendarImages.showCalendar(true);
    OlMapField.setVisibleFieldImage(true);
  }

  function cancelSaveField() {
    OlMap.removeDrawedField();
  }

  function onDeleteFieldFormSubmit(event) {
    event.preventDefault();

    const uuid = $('#deleteFieldModal').data('uuid');

    disableButton($('#fmdCancelBtn'));
    disableButton($('#fmdDeleteBtn'), true);

    $.post({
      url: `api/fields/${uuid}`,
      type: 'DELETE'
    }).done(() => { onDeleteFieldSuccess(uuid) }).fail((err) => {
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(() => {
      enableButton($('#fmdDeleteBtn,#fmdCancelBtn'));
    });
  }

  function onDeleteFieldSuccess(uuid) {
    $(`#field-${uuid}`).remove();

    OlMap.removeField(uuid);

    const processField = OlMapField.getProcessField();
    if (processField) {
      const isProcessField = processField.uuid == uuid;
      if (isProcessField) {
        Indices.showIndices(false);
        CalendarImages.showCalendar(false);
        OlMapField.removeFieldImage(false);
      }
    }

    $('#deleteFieldModal').modal('hide');
    toastr.success(`Se ha eliminado el lote exitosamente.`);
  }

  function onFieldFormSubmit(event) {
    event.preventDefault();
    const field = getFieldData();

    const action = $('#saveFieldModal').data('action');

    if (action === 'create') {
      insertField(field);
    } if (action === 'create-shapefile') {
      const wkt = $('#saveFieldModal').data('wkt');
      field.wkt = wkt;
      insertField(field);
    } else if (action === 'update') {
      const uuid = $('#saveFieldModal').data('uuid');
      updateField(uuid, field);
    }
  }

  function getFieldData() {
    return {
      name: emptyToNull(fieldName.val()),
      cropId: emptyToNull($('#fieldCrop').val()),
      plantingDate: emptyToNull($('#fieldPlantingDate').val()),
      harvestDate: emptyToNull($('#fieldHarvestDate').val()),
      borderColor: $('#fieldBorderColor').val(),
      borderSize: $('#fieldBorderSize').val(),
      wkt: OlMap.drawedWkt()
    };
  }

  function insertField(field) {
    disableButton($('#fmsCancelBtn'));
    disableButton($('#fmsSaveBtn'), true);

    $.post({
      url: `api/fields`,
      contentType: 'application/json'
    }, JSON.stringify(field)).done((field) => {
      resetFieldModal();

      OlMap.removeDrawedField();
      showField(field);

      $('#saveFieldModal').modal('hide');
      toastr.success(`Lote guardado exitosamente.`);
    }).fail(() => {
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(() => {
      enableButton($('#fmsSaveBtn,#fmsCancelBtn'));
    });
  }

  function updateField(uuid, field) {
    disableButton($('#fmsCancelBtn'));
    disableButton($('#fmsSaveBtn'), true);

    $.post({
      method: 'PUT',
      url: `api/fields/${uuid}`,
      contentType: 'application/json'
    }, JSON.stringify(field)).done((field) => {
      resetFieldModal();
      OlMap.updateFeatureField(field);

      $(`#field-${uuid}`).replaceWith($(templateFieldUI(field)));

      $('#saveFieldModal').modal('hide');
      toastr.success(`Lote actualizado exitosamente.`);
    }).fail(() => {
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(() => {
      enableButton($('#fmsSaveBtn,#fmsCancelBtn'));
    });
  }

  function activateDrawField() {
    OlMap.activateDrawField(() => {
      resetFieldModal();
      $('#saveFieldModal').data('action', 'create').modal('show');
      setTimeout(() => { fieldName.select(); }, 300);
    });
  }

  function resetFieldModal() {
    fieldName.val(null);
    $('#fieldCrop').val(null).trigger('change.select2');
    $('#fieldBorderColor').minicolors('value', '#00ff00');
    $('#fieldBorderSize').val(5);
    $('#fieldPlantingDate,#fieldHarvestDate').datepicker('update', '');
  }

  function getUserFields() {
    $('#userFieldsList').empty();

    $.get(`api/fields`, (fields) => {
      $('#userFieldsList').empty();
      fields.forEach(showField);

      OlMap.activeMapEvents(true);

      Tasks.getUserTasks();
    });
  }

  function showField(field) {
    OlMap.addField(field);
    addFieldToList(field);
  }

  function addFieldToList(field) {
    $('#userFieldsList').append($(templateFieldUI(field)));
  }

  function templateFieldUI(field) {
    return `
    <div id="field-${field.uuid}" class="px-2 py-1 justify-content-between align-items-center field-list-item" fieldname="${field.name}" uuid="${field.uuid}">
      <div class="d-flex align-items-center flex-grow-1">
        <div onclick="Fields.goToFieldInMap('${field.uuid}')" class="border p-2 rounded bg-dark"
          style="position: relative;opacity: 95%; cursor:pointer;">
          <i class="fas fa-seedling text-large text-white"></i>
          <i class="fas fa-seedling text-large"
            style="color: ${field.borderColor}; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);"></i>
        </div>
        <div class="flex-grow-1">
          <div class="d-flex align-items-center flex-grow-1">
            <div class="flex-grow-1">
              <p class="font-weight-bold m-0 text-truncate field-list-item-name">${field.name}
              </p>
              <div class="d-flex justify-content-between">
                <small class="font-weight-normal">${Measure.areaM2ToHa(field.areaM2)} ha</small>
                <a href="#" id="field-processing-${field.uuid}" class="font-weight-normal text-primary" style="display:none;">Procesando (7)</a>
              </div>
            </div>
            <div class="btn-group dropright">
              <button id="field-ddm-${field.uuid}" type="button"
                class="btn btn-sm btn-default icon-btn borderless rounded-pill md-btn-flat dropdown-toggle hide-arrow disabled-on-edit-geometry"
                data-toggle="dropdown" aria-expanded="true" data-boundary="viewport">
                <i class="fas fa-ellipsis-v"></i>
              </button>
              <div class="dropdown-menu" aria-labelledby="field-ddm-${field.uuid}">
                <a onclick="Fields.goToFieldInMap('${field.uuid}')" class="dropdown-item"
                  href="javascript:void(0)">
                  <i class="fas fa-map-pin"></i>&nbsp; Ubicar en mapa
                </a>
                <div class="dropdown-toggle">
                  <div class="dropdown-item">
                    <i class="fas fa-pencil-alt"></i>&nbsp; Editar
                  </div>
                  <div class="dropdown-menu" style="margin-left:-1px;">
                    <a class="dropdown-item" onclick="Fields.editGeometry('${field.uuid}')"
                      href="javascript:void(0)">
                      <i class="fas fa-vector-square"></i>&nbsp; Límites
                    </a>
                    <a class="dropdown-item" onclick="Fields.editAttributes('${field.uuid}')"
                      href="javascript:void(0)">
                      <i class="far fa-list-alt"></i>&nbsp; Datos y cultivo
                    </a>
                  </div>
                </div>
                <a onclick="Fields.deleteField('${field.uuid}','${field.name}')"
                  class="dropdown-item" href="javascript:void(0)">
                  <i class="fas fa-trash-alt"></i>&nbsp; Eliminar
                </a>
              </div>
            </div>
          </div>
          <div id="field-progress-${field.uuid}" class="progress" style="height: 4px;margin-right: 13px; display:none;">
            <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="100"
              aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
          </div>
        </div>
      </div>
    </div>
    `;
  }

  init();
  return element;
})({});