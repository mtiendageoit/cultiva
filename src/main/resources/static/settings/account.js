const AccountPassword = (() => {
  const currentPassword = $('#currentPassword');
  const newPassword = $('#newPassword');
  const matchPassword = $('#matchPassword');

  const changePassword = $('#changePassword');
  const changePasswordForm = $('#changePasswordForm');

  function init() {
    changePasswordForm.submit(onChangePasswordSubmit);
  }

  function onChangePasswordSubmit(e) {
    e.preventDefault();

    if (newPassword.val() !== matchPassword.val()) {
      matchPassword.focus();
      return toastr.warning(`Las contraseñas no coinciden.`);
    }

    const body = {
      password: currentPassword.val(),
      newPassword: newPassword.val()
    }

    disableButton(changePassword, true);
    $.post({
      url: `/api/settings/account?change-password`,
      contentType: 'application/json'
    }, JSON.stringify(body)).done(() => {
      currentPassword.val(null);
      newPassword.val(null);
      matchPassword.val(null);
      toastr.success('La contraseña ha sido actualizada');
    }).fail((error) => {
      const code = error.responseJSON.code;
      if (code === 'invalid-password') {
        currentPassword.focus();
        return toastr.warning('La contraseña actual es incorrecta.');
      }
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(() => {
      enableButton(changePassword);
    });

  }

  init();
})({});


const AccountDelete = (() => {
  const deletePhrase = 'Eliminar';
  const deletePhraseLabel = $('#deletePhraseLabel');
  const deletePhraseTxt = $('#deletePhraseTxt');
  const deleteAccountModal = $('#deleteAccountModal');
  const deleteAccountSuccessModal = $('#deleteAccountSuccessModal');
  const showDeleteAccountModalBtn = $('#showDeleteAccountModalBtn');
  const deleteAccountBtn = $('#deleteAccountBtn');
  const deleteAccountForm = $('#deleteAccountForm');
  const deleteAccountRedirectTime = $('#deleteAccountRedirectTime');

  function init() {
    deletePhraseLabel.text(`"${deletePhrase}"`);
    deletePhraseTxt.keyup(onDeletePhraseTxtKeyup);
    deleteAccountForm.submit(onDeleteAccountForm);
    showDeleteAccountModalBtn.click(onShowDeleteAccountBtnClick);
  }

  function onDeletePhraseTxtKeyup() {
    const text = deletePhraseTxt.val();
    deleteAccountBtn.attr('disabled', text !== deletePhrase);
  }

  function onDeleteAccountForm(e) {
    e.preventDefault();

    disableButton(deleteAccountBtn, true);
    $.post(`/api/settings/account?delete-account`).done(() => {
      deleteAccountModal.modal('hide');
      deleteAccountSuccessModal.modal('show');

      let seconds = 30;
      deleteAccountRedirectTime.text(seconds);
      setInterval(() => {
        if (seconds <= 0) {
          location.href = '/';
        }
        deleteAccountRedirectTime.text(seconds);
        seconds = seconds - 1;
      }, 1000);

    }).fail((error) => {
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(() => {
      enableButton(deleteAccountBtn);
    });

  }

  function onShowDeleteAccountBtnClick() {
    deletePhraseTxt.val(null);
    deleteAccountBtn.attr('disabled', true);
    deleteAccountModal.modal('show');
  }

  // function onDeleteAccountSubmit(e) {
  //   e.preventDefault();
  // }

  init();
})({});