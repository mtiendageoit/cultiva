const Indices = ((element) => {
  const INDICES = {
    NDVI: { id: 1, name: 'NDVI', min: '-1.0', max: '1.0', colors: ['#A52A2A', '#FFFF00', '#008000'] },
    MSAVI: { id: 2, name: 'MSAVI', min: '-1.0', max: '1.0', colors: ['#A10024', '#F3FCAB', '#0C7E43'] },
    RECI: { id: 3, name: 'RECI', min: '0.0', max: '10.0', colors: ['#A92D2A', '#FFFDC1', '#367C46'] },
    SIPI: { id: 4, name: 'SIPI', min: '0.0', max: '2.0', colors: ['#009392', '#D0587E', '#F1EAC8'] },
    NDWI: { id: 5, name: 'NDWI', min: '-1.0', max: '1.0', colors: ['#008000', '#FFFFFF', '#0000CC'] }
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
  const legendStatisticsStdDev = $('#legendStatisticsStdDev');
  const legendStatisticsVariance = $('#legendStatisticsVariance');
  const legendStatisticsP25 = $('#legendStatisticsP25');
  const legendStatisticsP50 = $('#legendStatisticsP50');
  const legendStatisticsP75 = $('#legendStatisticsP75');

  element.getSelectedIndiceStyle = () => {
    const indice = element.selectedIndice();
    if (INDICES.NDVI == indice) return ndviStyle();
    if (INDICES.MSAVI == indice) return msaviStyle();
    if (INDICES.RECI == indice) return reciStyle();
    if (INDICES.SIPI == indice) return sipiStyle();
    if (INDICES.NDWI == indice) return ndwiStyle();
  };

  function ndwiStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        -1.0, '#008000',
        0.0, '#FFFFFF',
        1.0, '#0000CC',
      ],
    };
  }

  function sipiStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0.0, '#009392',
        1.0, '#D0587E',
        2.0, '#F1EAC8',
      ],
    };
  }


  function reciStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        0.0, '#A92D2A',
        5.0, '#FFFDC1',
        10.0, '#367C46',
      ],
    };
  }

  function msaviStyle() {
    return {
      color: [
        'interpolate',
        ['linear'],
        ['band', 1],
        -9999, 'Transparent',
        -1.0, '#A10024',
        0, '#F3FCAB',
        1.0, '#0C7E43',
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
        -1.0, '#A52A2A',
        0, '#FFFF00',
        1.0, '#008000',
      ],
    };
  }

  element.showIndices = (show) => {
    if (show) {
      // legend.show();
    } else {
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
    legendStatisticsStdDev.text(loadingText);
    legendStatisticsVariance.text(loadingText);
    legendStatisticsP25.text(loadingText);
    legendStatisticsP50.text(loadingText);
    legendStatisticsP75.text(loadingText);

  };

  element.showIndiceFieldStatistics = (stats) => {
    legendStatisticsMax.text(formatNumber(stats.max));
    legendStatisticsMean.text(formatNumber(stats.mean));
    legendStatisticsMedian.text(formatNumber(stats.median));
    legendStatisticsMin.text(formatNumber(stats.min));
    legendStatisticsStdDev.text(formatNumber(stats.stdDev));
    legendStatisticsVariance.text(formatNumber(stats.variance));
    legendStatisticsP25.text(formatNumber(stats.p25));
    legendStatisticsP50.text(formatNumber(stats.p50));
    legendStatisticsP75.text(formatNumber(stats.p75));
    legend.show();
  };

  init();

  return element;
})({});