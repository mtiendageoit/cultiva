const Indices = ((element) => {
  const INDICES = {
    NDVI: { id: 1, name: 'NDVI', min: '-1.0', max: '1.0', colors: ['#FF0000', '#FFFFFF', '#008000'] },
    EVI: { id: 2, name: 'EVI', min: '0.0', max: '12.0', colors: ['#0000FF', '#FFFFFF', '#008000'] },
    AVI: { id: 3, name: 'AVI', min: '0.0', max: '1.0', colors: ['#0000FF', '#FFFFFF', '#008000'] },
    SAVI: { id: 4, name: 'SAVI', min: '-1.0', max: '1.0', colors: ['#A52A2A', '#FFFFFF', '#008000'] },
    MSI: { id: 5, name: 'MSI', min: '0.0', max: '2.0', colors: ['#A52A2A', '#FFFFFF', '#0000FF'] },
    VCI: { id: 6, name: 'VCI', min: '0.0', max: '100.0', colors: ['#FF0000', '#FFFF00', '#008000'] },
    VHI: { id: 7, name: 'VHI', min: '0.0', max: '1.0', colors: ['#A52A2A', '#FFFF00', '#008000'] }
  }
  const indicesBtns = $('.indice-menu-item');
  const selectedIndiceBtn = $('#selectedIndiceBtn');
  const container = $('#indicesListContainer');
  const legendTitleBar = $('#legendTitleBar');
  const legendBody = $('#legendBody');
  const legend = $('#legendContainer');
  const legendIndicenName = $('#legendIndicenName');
  const legendIndiceMax = $('#legendIndiceMax');
  const legendIndiceMin = $('#legendIndiceMin');
  const legendStatisticsMax = $('#legendStatisticsMax');
  const legendStatisticsMean = $('#legendStatisticsMean');
  const legendStatisticsMedian = $('#legendStatisticsMedian');
  const legendStatisticsMin = $('#legendStatisticsMin');

  element.getSelectedIndiceStyle = () => {
    const indice = element.selectedIndice();
    if (INDICES.NDVI == indice) return ndviStyle();
    if (INDICES.EVI == indice) return eviStyle();
    if (INDICES.AVI == indice) return aviStyle();
    if (INDICES.SAVI == indice) return saviStyle();
    if (INDICES.MSI == indice) return msiStyle();
    if (INDICES.VCI == indice) return vciStyle();
    if (INDICES.VHI == indice) return vhiStyle();
  };

  function vhiStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0, '#A52A2A',
        0.5, '#FFFF00',
        1, '#008000',
      ],
    };
  }

  function vciStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0, '#FF0000',
        50, '#FFFF00',
        100, '#008000',
      ],
    };
  }

  function msiStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0, '#A52A2A',
        1, '#FFFFFF',
        2, '#0000FF',
      ],
    };
  }

  function saviStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        -1, '#A52A2A',
        0, '#FFFFFF',
        1, '#008000',
      ],
    };
  }

  function aviStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0, '#0000FF',
        0.5, '#FFFFFF',
        1, '#008000',
      ],
    };
  }

  function eviStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0, '#0000FF',
        6, '#FFFFFF',
        12, '#008000',
      ],
    };
  }

  function ndviStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        -1.0, '#a52a2a',
        0, '#ffff00',
        1.0, '#008000',
      ],
    };
  }

  element.showIndices = (show) => {
    if (show) {
      container.show();
      legend.show();
    } else {
      container.hide();
      legend.hide();
    }
  };

  element.selectedIndice = () => {
    const id = selectedIndiceBtn.attr('indice');
    return Object.values(INDICES).find(item => item.id == id);
  }

  function init() {
    indicesBtns.click(onIndiceClick);
    legendTitleBar.click(onLegendTitleBarClick)

    selectFirstIndice();
  }

  function onLegendTitleBarClick() {
    legendBody.toggle();

    if (legendBody.is(':visible')) {
      legendTitleBar.find('i').removeClass('fa-angle-double-up').addClass('fa-angle-double-down');
    } else {
      legendTitleBar.find('i').removeClass('fa-angle-double-down').addClass('fa-angle-double-up');
    }

  }

  function selectFirstIndice() {
    const firstId = indicesBtns.first().attr('indice')
    selectIndice(firstId);
  }

  function selectIndice(id) {
    indicesBtns.removeClass('active');
    indicesBtns.find('i').hide();

    const indiceBtn = indicesBtns.filter(`[indice='${id}']`);
    const name = $(indiceBtn).attr('indice-name');
    selectedIndiceBtn.text(name);
    selectedIndiceBtn.attr('indice', id);

    indiceBtn.find('i').show();
    indiceBtn.addClass('active');
  }

  function onIndiceClick() {
    const indice = $(this).attr('indice');
    selectIndice(indice);

    OlMapField.getImageForSelectedField();
  }

  element.loadingLegend = () => {
    const indice = element.selectedIndice();
    legendIndicenName.text(indice.name);
    legendIndiceMax.text(indice.max);
    legendIndiceMin.text(indice.min);

    const background = `linear-gradient(0deg, ${indice.colors[0]} 0%, ${indice.colors[1]} 50%, ${indice.colors[2]} 100%);`;
    document.getElementsByClassName('indice-legend-colors')[0].setAttribute("style", `background:${background}`);

    const loadingText = '-------';
    legendStatisticsMax.text(loadingText);
    legendStatisticsMean.text(loadingText);
    legendStatisticsMedian.text(loadingText);
    legendStatisticsMin.text(loadingText);
  };

  element.showIndiceFieldStatistics = (stats) => {
    legendStatisticsMax.text(formatNumber(stats.max));
    legendStatisticsMean.text(formatNumber(stats.mean));
    legendStatisticsMedian.text(formatNumber(stats.median));
    legendStatisticsMin.text(formatNumber(stats.min));
  };

  init();

  return element;
})({});