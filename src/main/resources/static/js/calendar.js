const Calendar = (function (element) {
  const CurrentOrders = [];
  const calendarImages = $('#calendarImages');

  element.showOrders = (orders) => {
    const selected = calendarImages.datepicker('getDate');
    CurrentOrders.length = 0;
    if(orders){
      CurrentOrders.push(...orders);
    }
    calendarImages.datepicker('update', selected);
  };

  element.getSelectedDate = () => {
    return formatDate(calendarImages.datepicker('getDate'));
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
      endDate: '0',
      startDate: new Date(2023, 0, 1),
      format: 'dd/mm/yyyy',
      orientation: 'bottom right',
      container: '#calendarImagesContainer',
      beforeShowDay: beforeShowDay,
    }).on('changeDate', onChangeDate);
  }

  function beforeShowDay(date) {
    if (CurrentOrders.length > 0) {
      const currentDate = formatDate(date);
      const order = CurrentOrders.find(order => order.imageDate == currentDate);
      if (order) {
        return {
          content: dayTemplate(date.getDate(), OrderStatus.from(order.status).cssIcon)
        };
      }
    }
  }

  function dayTemplate(day, cssIcon) {
    return `
      <div>
        <b>${day}</b>
        <i class="${cssIcon}" style="position:absolute;font-size: xx-small;"></i>
      </div>
    `;
  }

  function onChangeDate(e) {
    OlMapField.getImageForSelectedField();
  }

  function formatDate(date) {
    if (date) {
      return new Date(date).toLocaleDateString("es-MX", {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    }
  }

  init();
  return element;
})({});