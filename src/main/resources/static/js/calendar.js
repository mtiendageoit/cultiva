const CalendarImages = (function (element) {
  const calendarImages = $('#calendarImages');
  const container = $('#calendarImagesContainer');
  let currentDates;

  element.showCalendar = (show) => {
    if (show) container.show();
    else container.hide();
  };

  element.setDates = (dates) => {
    const hasDates = dates && dates.length > 0;

    if (hasDates) {
      currentDates = dates;
      const startDate = currentDates[currentDates.length - 1].imageDate;
      calendarImages.datepicker('setStartDate', startDate);

      const endDate = currentDates[0].imageDate;
      calendarImages.datepicker('setEndDate', endDate);
    }

    element.showCalendar(hasDates);
    Indices.showIndices(hasDates);
  };

  element.getDate = () => {
    return formatDate(calendarImages.datepicker('getDate'));
  };

  element.select = (date) => {
    calendarImages.datepicker("update", date);
  };

  function init() {
    initControls();
  }

  function initControls() {
    calendarImages.datepicker({
      autoclose: true,
      todayBtn: true,
      language: 'es',
      todayHighlight: false,
      format: 'dd/mm/yyyy',
      orientation: 'bottom right',
      container: '#calendarImagesContainer',
    }).on('changeDate', onChangeDate);
  }

  function onChangeDate(e) {
    OlMapField.getImageForSelectedField();
  }

  function formatDate(date) {
    return new Date(date).toLocaleDateString("es-MX", {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  init();
  return element;
})({});