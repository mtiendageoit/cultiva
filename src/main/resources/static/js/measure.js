const Measure = (function (element) {
  let olMap;
  const measureLayerType = 'measure-layer';
  const measureTypeEnum = { LineString: 'LineString', Polygon: 'Polygon' };
  const measureUnit = { M: 'M', M2: 'M2', KM: 'KM', KM2: 'KM2' };

  const init = () => {
    $('#measureBtn,#measureAreaBtn').click(onMeasureClick);
    $('#measureTypeMeters').click(onMeasureTypeMeters);
    $('#measureTypeKilometers').click(onMeasureTypeKiloMeters);
  };

  function onMeasureTypeMeters() {
    $('#measureTypeMeters').addClass('active');
    $('#measureTypeKilometers').removeClass('active');
    refreshVectorLayer();
  }

  function onMeasureTypeKiloMeters() {
    $('#measureTypeKilometers').addClass('active');
    $('#measureTypeMeters').removeClass('active');
    refreshVectorLayer();
  }

  function onMeasureClick() {
    $(this).toggleClass('active');

    let otherMeasure;

    if ($(this).attr('measure') == measureTypeEnum.LineString) {
      otherMeasure = $('#measureAreaBtn');
      $('#measureTypeMeters').text('m');
      $('#measureTypeKilometers').text('km');
    } else {
      otherMeasure = $('#measureBtn');
      $('#measureTypeMeters').text('m²');
      $('#measureTypeKilometers').text('km²');
    }

    const active = $(this).hasClass('active');
    if (active) {
      $('#measureTypes').css('opacity', 1);
      otherMeasure.removeClass('active');
      element.resetControl();
    } else {
      $('#measureTypes').css('opacity', 0);
      element.remove();
    }


    OlMapField.activeMouseEvents(!active);
  }

  element.isActive = () => {
    return olMap.getLayers().getArray().find(layer => layer.get('type') === measureLayerType) !== undefined;
  }

  element.remove = () => {
    olMap.removeInteraction(draw);
    olMap.removeLayer(vector);
    source.clear();
    olMap.removeLayer(vector);
  }

  element.resetControl = () => {
    element.remove();
    olMap.addLayer(vector);
    addInteraction(getMeasureType());
  }

  const refreshVectorLayer = () => {
    vector.changed();
  }

  const getMeasureType = () => {
    const lenActive = $('#measureBtn').hasClass('active');
    return lenActive ? measureTypeEnum.LineString : measureTypeEnum.Polygon;
  }

  const getMeasureUnits = () => {
    const type = getMeasureType();
    if (type === measureTypeEnum.LineString) {
      return $('#measureTypeMeters').hasClass('active') ? measureUnit.M : measureUnit.KM;
    } else if (type === measureTypeEnum.Polygon) {
      return $('#measureTypeMeters').hasClass('active') ? measureUnit.M2 : measureUnit.KM2;
    }
  }

  const style = new ol.style.Style({
    fill: new ol.style.Fill({
      color: 'rgba(255, 255, 255, 0.2)',
    }),
    stroke: new ol.style.Stroke({
      color: 'rgba(255, 255, 255, 1)',
      lineDash: [10, 10],
      width: 2,
    }),
    image: new ol.style.Circle({
      radius: 5,
      stroke: new ol.style.Stroke({
        color: 'rgba(0, 0, 0, 0.7)',
      }),
      fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 0.2)',
      }),
    }),
  });

  const labelStyle = new ol.style.Style({
    text: new ol.style.Text({
      font: '14px Calibri,sans-serif',
      fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 1)',
      }),
      backgroundFill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.7)',
      }),
      padding: [3, 3, 3, 3],
      textBaseline: 'bottom',
      offsetY: -15,
    }),
    image: new ol.style.RegularShape({
      radius: 8,
      points: 3,
      angle: Math.PI,
      displacement: [0, 10],
      fill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.7)',
      }),
    }),
  });

  const tipStyle = new ol.style.Style({
    text: new ol.style.Text({
      font: '12px Calibri,sans-serif',
      fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 1)',
      }),
      backgroundFill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.4)',
      }),
      padding: [2, 2, 2, 2],
      textAlign: 'left',
      offsetX: 15,
    }),
  });

  const modifyStyle = new ol.style.Style({
    image: new ol.style.Circle({
      radius: 5,
      stroke: new ol.style.Stroke({
        color: 'rgba(0, 0, 0, 0.7)',
      }),
      fill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.4)',
      }),
    }),
    text: new ol.style.Text({
      text: 'Arrastra para modificar',
      font: '12px Calibri,sans-serif',
      fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 1)',
      }),
      backgroundFill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.7)',
      }),
      padding: [2, 2, 2, 2],
      textAlign: 'left',
      offsetX: 15,
    }),
  });

  const segmentStyle = new ol.style.Style({
    text: new ol.style.Text({
      font: '12px Calibri,sans-serif',
      fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 1)',
      }),
      backgroundFill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.4)',
      }),
      padding: [2, 2, 2, 2],
      textBaseline: 'bottom',
      offsetY: -12,
    }),
    image: new ol.style.RegularShape({
      radius: 6,
      points: 3,
      angle: Math.PI,
      displacement: [0, 8],
      fill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.4)',
      }),
    }),
  });

  const segmentStyles = [segmentStyle];

  const formatLength = function (line) {
    let output;
    const unit = getMeasureUnits();
    const length = ol.sphere.getLength(line);
    if ([measureUnit.M, measureUnit.M2].includes(unit)) {
      output = formatNumber(Math.round(length * 100) / 100) + ' m';
    } else {
      output = formatNumber(Math.round((length / 1000) * 100) / 100) + ' km';
    }
    return output;
  };

  const formatArea = function (polygon) {
    const area = ol.sphere.getArea(polygon);
    let output;

    const unit = getMeasureUnits();
    if (unit === measureUnit.M2) {
      output = formatNumber(Math.round(area * 100) / 100) + ' m\xB2';
    } else {
      output = formatNumber(Math.round((area / 1000000) * 100) / 100) + ' km\xB2';
    }

    output += ` | ${element.areaM2ToHa(area)} ha`;

    return output;
  };

  const source = new ol.source.Vector();

  const modify = new ol.interaction.Modify({ source: source, style: modifyStyle });

  let tipPoint;

  function styleFunction(feature, segments, drawType, tip) {

    const styles = [style];
    const geometry = feature.getGeometry();
    const type = geometry.getType();
    let point, label, line;
    if (!drawType || drawType === type) {
      if (type === measureTypeEnum.Polygon) {
        point = geometry.getInteriorPoint();
        label = formatArea(geometry);
        line = new ol.geom.LineString(geometry.getCoordinates()[0]);
      } else if (type === measureTypeEnum.LineString) {
        point = new ol.geom.Point(geometry.getLastCoordinate());
        label = formatLength(geometry);
        line = geometry;
      }
    }
    if (segments && line) {
      let count = 0;
      line.forEachSegment(function (a, b) {
        const segment = new ol.geom.LineString([a, b]);
        const label = formatLength(segment);
        if (segmentStyles.length - 1 < count) {
          segmentStyles.push(segmentStyle.clone());
        }
        const segmentPoint = new ol.geom.Point(segment.getCoordinateAt(0.5));
        segmentStyles[count].setGeometry(segmentPoint);
        segmentStyles[count].getText().setText(label);
        styles.push(segmentStyles[count]);
        count++;
      });
    }
    if (label) {
      labelStyle.setGeometry(point);
      labelStyle.getText().setText(label);
      styles.push(labelStyle);
    }
    if (
      tip &&
      type === 'Point' &&
      !modify.getOverlay().getSource().getFeatures().length
    ) {
      tipPoint = geometry;
      tipStyle.getText().setText(tip);
      styles.push(tipStyle);
    }
    return styles;
  }

  const vector = new ol.layer.Vector({
    zIndex: 2,
    source: source,
    type: measureLayerType,
    style: function (feature) {
      return styleFunction(feature, true);
    },
  });

  let draw; // global so we can remove it later

  function addInteraction(drawType) {
    const activeTip = '';
    const idleTip = 'Click para comenzar a medir';
    let tip = idleTip;
    draw = new ol.interaction.Draw({
      source: source,
      type: drawType,
      style: function (feature) {
        return styleFunction(feature, true, drawType, tip);
      },
    });
    draw.on('drawstart', function () {
      const clearPrevious = true;
      if (clearPrevious) {
        source.clear();
      }
      modify.setActive(false);
      tip = activeTip;
    });
    draw.on('drawend', function () {
      modifyStyle.setGeometry(tipPoint);
      modify.setActive(true);
      olMap.once('pointermove', function () {
        modifyStyle.setGeometry();
      });
      tip = idleTip;
    });
    modify.setActive(true);
    olMap.addInteraction(draw);
  }

  element.initMeasure = (map) => {
    olMap = map;
    olMap.addInteraction(modify);
  }

  element.areaM2ToHa = (areaM2) => {
    return formatNumber(Math.round(areaM2 * 100) / 100 / 10_000);
  }

  init();

  return element;
})({});