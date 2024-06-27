const CalendarImages = (function (element) {
  const DayStatus = { success: 'fas fa-cog', queued: 'fas fa-cog', failed: 'fas fa-ban' }
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
      beforeShowDay: beforeShowDay,
    }).on('changeDate', onChangeDate)
      .on('changeMonth', onChangeMonth);
  }

  function beforeShowDay(date) {
    const day = date.getDate();
    switch (date.getDate()) {
      case 4:
        return {
          content: `
              <div>
                <b>${day}</b>
                <i class="fas fa-cog" style="position:absolute;font-size: xx-small;"></i>
              </div>
            `
        };
      case 6:
      case 7:
        return {
          content: `
            <div>
              <b>${day}</b>
              <i class="fas fa-check" style="position:absolute;font-size: xx-small;"></i>
            </div>
          `
        };
      case 8:
        return {
          content: `
            <div>
              <b>${day}</b>
              <i class="fas fa-ban" style="position:absolute;font-size: xx-small;"></i>
            </div>
          `
        };
      case 12:
        return "green";
    }
  }

  function dayTemplate(day, dayStatus) {
    return `
      <div>
        <b>${day}</b>
        <i class="${dayStatus}" style="position:absolute;font-size: xx-small;"></i>
      </div>
    `;
  }

  function onChangeMonth(e) {
    console.log(e);
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