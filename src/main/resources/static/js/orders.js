const OrderStatus = {
  success: { cssIcon: 'fas fa-check' },
  queued: { cssIcon: 'fas fa-cog' },
  failed: { cssIcon: 'fas fa-ban' },
  from(status) {
    if (status == 'success') return OrderStatus.success;
    if (status == 'queued') return OrderStatus.queued;
    if (status == 'failed') return OrderStatus.failed;
  }
}

const Orders = ((element) => {
  let timer;
  const refreshMillis = 20_000; //20 seconds

  const ordersModal = $('#ordersModal');
  const fieldFilter = $('#fomFieldFilter');
  const statusFilter = $('#fomStatusFilter');
  const showAllFilterBtn = $('#fomShowAllFilterBtn');
  let dataTable;

  function init() {
    fieldFilter.change(filterOrders);
    statusFilter.change(filterOrders);
    showAllFilterBtn.click(onShowAllFilterClick);
    initOrdersTable();
  }

  function onShowAllFilterClick() {
    fieldFilter.val(-1);
    statusFilter.val(-1);
    filterOrders();
  }

  function filterOrders() { dataTable.api().ajax.reload(); }

  function initOrdersTable() {
    dataTable = $('#fomOrdersTable').dataTable({
      columns: [{
        data: 'field'
      }, {
        data: 'imageDate'
      }, {
        data: 'statusStr'
      }, {
        data: 'createdAt'
      }, {
        data: 'completedAt'
      }, {
        data: 'elapsedTime'
      }],
      deferLoading: true,
      responsive: true,
      searching: false,
      lengthChange: false,
      pageLength: 10,
      ordering: false,
      paging: true,
      info: false,
      serverSide: true,
      language: {
        paginate: {
          next: "Siguiente",
          previous: "Anterior"
        },
        emptyTable: "Sin ordenes que mostrar",
      },
      ajax: {
        url: `api/orders/filter`,
        type: "GET",
        data: function (params) {
          const fieldUuid = fieldFilter.val();
          const status = statusFilter.val();
          const rParams = $.extend({}, params, {
            'page': params.start / params.length,
            'size': params.length
          });

          if (fieldUuid != -1) { rParams.fieldUuid = fieldUuid; }
          if (status != -1) { rParams.status = status; }

          return rParams;
        },
        dataSrc: function (response) {
          const data = response.data;
          data.forEach(item => {
            const statusStr = item.status == 'queued' ? 'Processing' : item.status;
            const status = `<i class="${OrderStatus.from(item.status).cssIcon}"></i> ${statusStr}`
            const field = `<i class="fas fa-seedling text-large" style="color: ${item.fieldBorderColor};"></i>  ${item.fieldName}`;

            const createdAt = parseDate(item.createdAt);
            const completedAt = (item.completedAt) ? parseDate(item.completedAt) : new Date();
            const elapsedTime = (completedAt - createdAt) / 1000 / 60;

            item.statusStr = status;
            item.field = field;
            item.elapsedTime = `${formatNumber(elapsedTime, 1)} Minutos`;
          });
          return data;
        }
      }
    });
  }

  function parseDate(dateString) {
    const [datePart, timePart] = dateString.split(' ');
    const [day, month, year] = datePart.split('/').map(Number);
    const [hours, minutes, seconds] = timePart.split(':').map(Number);
    return new Date(year, month - 1, day, hours, minutes, seconds);
  }

  element.showOrdersField = (fieldUuid, status = 'queued') => {
    const fields = getFieldsInList();
    fieldFilter.empty();
    fieldFilter.append(new Option('Todos', -1));
    fields.forEach(field => fieldFilter.append(new Option(field.fieldname, field.uuid)));

    fieldFilter.val(fieldUuid);
    statusFilter.val(status);

    filterOrders();

    ordersModal.modal('show');
  };

  function getFieldsInList() {
    const fields = $('#userFieldsList .field-list-item').map(function () {
      return {
        fieldname: $(this).attr('fieldname'),
        uuid: $(this).attr('uuid')
      };
    }).get();
    return fields;
  }

  element.getOrdersAndCount = (field, callback) => {
    if (timer) {
      clearInterval(timer);
    }

    getOrdersAndCount(field?.uuid, (response) => {
      if (callback) { callback(response); }
      enableAutoRefresh();
    });
  };

  function enableAutoRefresh() {
    timer = setInterval(() => {
      const field = OlMapField.getSelectedField();
      getOrdersAndCount(field?.uuid, (_) => { })
    }, refreshMillis);
  }

  function getOrdersAndCount(fieldUuid, callback) {
    let url = 'api/orders/count-field';
    if (fieldUuid) {
      url += `?fieldUuid=${fieldUuid}`;
    }
    $.get(url, (response) => {
      if (response.fieldOrders) {
        response.fieldOrders.forEach(order => (order.fieldUuid = fieldUuid));
      }
      if (callback) { callback(response); }
      Calendar.showOrders(response.fieldOrders);
      showUIFieldOrderProcessing(response.ordersCount);
    });
  }

  function showUIFieldOrderProcessing(orders) {
    $('#userFieldsList .field-list-item').each((_, item) => {
      const uuid = $(item).attr('uuid');
      const order = orders.find(item => item.fieldUuid === uuid);
      const processingLink = $(`#field-processing-${uuid}`);
      const processingProgess = $(`#field-progress-${uuid}`);

      const mnuDelete = $(`#field-menu-item-delete-${uuid}`);
      const mnuEditGeometry = $(`#field-menu-item-edit-geometry-${uuid}`);
      const mnuEditAttributes = $(`#field-menu-item-edit-attributes-${uuid}`);
      if (order) {
        processingLink.text(`Procesando (${order.count})`).show();
        processingProgess.show();
        mnuDelete.addClass('disabled');
        mnuEditGeometry.addClass('disabled');
        mnuEditAttributes.addClass('disabled');
      } else {
        processingLink.text('').hide();
        processingProgess.hide();

        mnuDelete.removeClass('disabled');
        mnuEditGeometry.removeClass('disabled');
        mnuEditAttributes.removeClass('disabled');
      }
    });
  }

  init();
  return element;
})({});