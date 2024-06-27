const Tasks = ((element) => {
  const userTasks = [];
  let isBusy = false;

  element.getUserTasks = () => {
    getTask();
  };

  function init() {
    setInterval(() => {
      if (!isBusy) {
        getTask();
      }
    }, 20_000);
  }

  function getTask() {
    isBusy = true;
    $.get(`api/tasks/count`, (tasks) => {
      userTasks.length = 0;
      userTasks.push(...tasks);
      showUIFieldTask();
      isBusy = false;
    });
  }

  function showUIFieldTask() {
    $('#userFieldsList .field-list-item').each((_, item) => {
      const uuid = $(item).attr('uuid');
      const tasks = userTasks.find(task => task.fieldUuid === uuid);
      const processingLink = $(`#field-processing-${uuid}`);
      const processingProgess = $(`#field-progress-${uuid}`);
      if (tasks) {
        processingLink.text(`Procesando (${tasks.tasksCount})`).show();
        processingProgess.show();
      } else {
        processingLink.text('').hide();
        processingProgess.hide();
      }
    });
  }

  init();

  return element;
})({});