const Weather = ((element) => {
  const dayContainer = $('#dayWeatherContainer');
  const weatherModal = $('#weatherModal');
  const weatherCharts = $('.weatherVariable');


  function init() {
    dayContainer.click(onDayContainerClick);
    weatherCharts.click(onWeatherChartsClick);

    // onDayContainerClick();
  }

  function onWeatherChartsClick() {
    weatherCharts.parent().removeClass('btn-success').addClass('btn-default');
    $(this).parent().removeClass('btn-default').addClass('btn-success');

    $('#temperatureChart,#humidityChart,#precipitationChart,#windChart').hide();
    const chart = $(this).attr('chart');
    $(`#${chart}`).show();
  }

  function resetModal() {
    $('#humidityChart,#precipitationChart,#windChart').hide();
    $('#temperatureChart').show();
  }

  function onDayContainerClick() {
    weatherModal.modal('show');

    element.renderTemperatureChart([]);
    element.renderHumidityChart([]);
    element.renderPrecipitationChart([]);
    element.renderWindChart([]);
  }

  element.renderTemperatureChart = (data) => {
    Highcharts.chart('temperatureChart', {
      title: { text: '' },
      credits: { enabled: false },
      xAxis: { type: 'datetime' },
      tooltip: { valueSuffix: ' °C' },
      yAxis: { title: { text: 'Temperatura °C' }, labels: { format: '{value}°' } },
      plotOptions: { series: { pointStart: Date.UTC(2022, 6, 30), pointInterval: 36e5 * 2 } },
      series: [{
        color: '#FFA726',
        type: 'areaspline',
        name: 'Temperatura',
        data: [5, 10, 15, 25, 20, 23, 7, 10, 15, 40, 35, 24, 20],
        fillColor: {
          stops: [[0, '#FFE1B6'], [1, '#FFFCF7']],
          linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 }
        }
      }]
    });
  };

  element.renderHumidityChart = (data) => {
    Highcharts.chart('humidityChart', {
      title: { text: '' },
      credits: { enabled: false },
      xAxis: { type: 'datetime' },
      tooltip: { valueSuffix: ' %' },
      yAxis: { title: { text: 'Humedad' }, labels: { format: '{value} %' } },
      plotOptions: { series: { pointStart: Date.UTC(2022, 6, 30), pointInterval: 36e5 * 2 } },
      series: [{
        name: 'Humedad',
        color: '#A774F9',
        type: 'areaspline',
        data: [2, 20, 42, 10, 25, 6, 17, 50, 17, 20, 37, 12, 20],
        fillColor: {
          stops: [[0, '#C6A3FF'], [1, '#FFFCF7']],
          linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 }
        }
      }]
    });
  };

  element.renderPrecipitationChart = (data) => {
    Highcharts.chart('precipitationChart', {
      title: { text: '' },
      credits: { enabled: false },
      xAxis: { type: 'datetime' },
      tooltip: { valueSuffix: ' mm' },
      yAxis: { title: { text: 'Precipitación' }, labels: { format: '{value} mm' } },
      plotOptions: { series: { pointStart: Date.UTC(2022, 6, 30), pointInterval: 36e5 * 2 } },
      series: [{
        type: 'column',
        name: 'Precipitación',
        color: Highcharts.getOptions().colors[0],
        data: [27.6, 28.8, 21.7, 34.1, 29.0, 28.4, 45.6, 51.7, 39.0, 60.0, 28.6, 32.1, 10]
      }]
    });
  };

  element.renderWindChart = (data) => {
    data = [
      [4.9, 90],
      [4.1, 242],
      [3.2, 262],
      [1.5, 284],
      [1.1, 294],
      [0.4, 192],
      [0.2, 30],
      [1.1, 110],
      [1.4, 112],
      [2.1, 132],
      [1.6, 134],
      [1.5, 128],
      [0.7, 91]
    ];
    Highcharts.chart('windChart', {
      title: { text: '' },
      credits: { enabled: false },
      xAxis: { type: 'datetime', offset: 40 },
      yAxis: { title: { text: 'Velocidad de viento (m/s)' } },
      plotOptions: { series: { pointStart: Date.UTC(2022, 0, 30), pointInterval: 36e5 * 2 } },
      series: [{
        data: data,
        type: 'windbarb',
        name: 'Velocidad de viento',
        showInLegend: false,
        tooltip: {
          valueSuffix: ' m/s',
          pointFormat: '<span style="color:{point.color}">●</span> {series.name}: <b>{point.value}</b><br/>'
        },
        color: Highcharts.getOptions().colors[1],
      }, {
        data: data,
        keys: ['y'],
        type: 'areaspline',
        name: 'Velocidad de viento',
        tooltip: { valueSuffix: ' m/s' },
        states: { inactive: { opacity: 1 } },
        color: Highcharts.getOptions().colors[0],
        fillColor: {
          linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },
          stops: [
            [0, Highcharts.getOptions().colors[0]],
            [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0.25).get()]
          ]
        },
      }]
    });
  };

  /*element.renderChart = (data) => {
    Highcharts.chart('weatherChartContainer', {
      title: { text: '' },
      credits: { enabled: false },
      xAxis: { type: 'datetime', offset: 40 },
      yAxis: [
        { title: { text: 'Temperatura °C' }, labels: { format: '{value}°' } },
        {
          title: { text: 'Humedad' },
          labels: { format: '{value} %' },
        },
        {
          title: { text: 'Precipitación' },
          labels: { format: '{value} mm' },
          opposite: true
        }
      ],
      plotOptions: {
        series: {
          pointStart: Date.UTC(2022, 6, 30),
          pointInterval: 36e5 * 2
        }
      },
      tooltip: {
        pointFormatter: function () {
          return `<span style=\"color:${this.color}\">●</span> ${this.series.name}: <b>${this.y}</b> <span style="opacity: 0.5"></span><br/>`;
        },
        shared: true
      },
      series: [
        {
          yAxis: 1,
          type: 'column',
          name: 'Precipitación',
          color: Highcharts.getOptions().colors[0],
          data: [27.6, 28.8, 21.7, 34.1, 29.0, 28.4, 45.6, 51.7, 39.0, 60.0, 28.6, 32.1, 10],
          tooltip: { valueSuffix: ' mm' }
        },
        {
          yAxis: 2,
          type: 'spline',
          name: 'Humedad',
          color: Highcharts.getOptions().colors[4],
          data: [2, 20, 42, 10, 25, 6, 17, 50, 17, 20, 37, 12, 20],
          tooltip: { valueSuffix: ' %' }
        },
        {
          type: 'spline',
          data: [5, 10, 15, 25, 20, 23, 7, 10, 15, 40, 35, 24, 20],
          color: '#ffa726',
          // fillColor: {
          //   linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },
          //   stops: [
          //     [0, '#ffe1b6'],
          //     [1, '#fffcf7'],
          //   ]
          // },
          name: 'Temperatura',
          tooltip: { valueSuffix: ' °C' },
        }, {
          type: 'windbarb',
          data: [
            [4.9, 90],
            [4.1, 242],
            [3.2, 262],
            [1.5, 284],
            [1.1, 294],
            [0.4, 192],
            [0.2, 30],
            [1.1, 110],
            [1.4, 112],
            [2.1, 132],
            [1.6, 134],
            [1.5, 128],
            [0.7, 91]
          ],
          name: 'Viento',
          color: Highcharts.getOptions().colors[1],
          showInLegend: false,
          tooltip: {
            valueSuffix: ' m/s'
          }
        }]
    });
  };*/

  init();

  return element;
})({});