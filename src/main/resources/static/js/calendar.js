const CalendarImages = (function (element) {
  const calendarImages = $('#calendarImages');
  const container = $('#calendarImagesContainer');
  let currentDates;

  element.showCalendar = (show) => {
    if (show) container.show();
    else container.hide();
  };

  element.setDates = (dates) => {
    disabled();
    const hasDates = dates && dates.length > 0;

    if (hasDates) {
      currentDates = dates;
      const startDate = currentDates[currentDates.length - 1].imageDate;
      calendarImages.datepicker('setStartDate', startDate);
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
    disabled();
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
      beforeShowDay: beforeShowDay
    }).on('changeDate', onChangeDate);
  }

  function onChangeDate(e) {
    OlMapField.getImageForSelectedField();
  }

  function beforeShowDay(date) {
    if (currentDates) {
      const showDate = formatDate(date);
      const imageDate = currentDates.find(item => item.imageDate === showDate);

      return {
        enabled: imageDate ? true : false
      }
    }

    return {
      enabled: false
    }
  }

  function disabled() {
    let startDate, endDate;
    startDate = endDate = formatDate(new Date());

    calendarImages.datepicker('setStartDate', startDate);
    calendarImages.datepicker('setEndDate', endDate);
    calendarImages.datepicker('setDatesDisabled', startDate);

    fieldImageDates = null;
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