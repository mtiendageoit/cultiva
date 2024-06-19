const ProfileGeneral = ((element) => {
  const settings = {
    avatar: {
      maxFileSizeMB: 1,
      validExts: ['PNG', 'JPG', 'GIF']
    }
  };

  const phone = $('#phone');
  const email = $('#email');
  const fullname = $('#fullname');
  const saveGeneral = $('#saveGeneral');

  const profileName = $('.principal-name');
  const avatarFile = $('#avatarFile');
  const updateAvatarBtn = $('#updateAvatarBtn');
  const userAvatarImg = $('.userAvatarImg');

  function init() {
    initUI();
    getProfile();
  }

  function initUI() {
    avatarFile.change(onAvatarFileChange);
    $('#generalInfo').submit(generalInfoSubmit);
  }

  function onAvatarFileChange() {
    const files = avatarFile[0].files;
    if (files && files.length > 0) {
      const file = files[0];
      const validExt = validateFileExt(file, settings.avatar.validExts);
      if (!validExt.valid) {
        return toastr.warning(`El archivo es inválido, seleccione un archivo de tipo "${settings.avatar.validExts.join(', ')}".`);
      }
      const validSize = validateMaxFileSizeMB(file, settings.avatar.maxFileSizeMB);
      if (!validSize.valid) {
        return toastr.warning(`El tamaño del archivo debe ser menor a ${settings.avatar.maxFileSizeMB}MB, seleccione uno de menor tamaño. (Tamaño del archivo seleccionado ${validSize.fileSizeMB}MB)`);
      }

      const body = new FormData();
      body.append('file', file);

      disableButton(updateAvatarBtn, true);
      $.post({
        url: `/api/settings/profile?avatar`,
        processData: false,
        contentType: false,
      }, body).done((avatarUrl) => {
        userAvatarImg.attr('src', avatarUrl);
      }).fail(function (error) {
        const code = error.responseJSON.code;
        if (code === 'file-not-allowed') {
          return toastr.warning(`El archivo es inválido, seleccione un archivo de tipo "${settings.avatar.validExts.join(', ')}".`);
        }

        if (code === 'file-size-exceeded') {
          return toastr.warning(`El tamaño del archivo debe ser menor a ${settings.avatar.maxFileSizeMB}MB.`);
        }

        toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
      }).always(function () {
        enableButton(updateAvatarBtn);
      });

    }

    avatarFile.val(null);
  }

  function generalInfoSubmit(e) {
    e.preventDefault();

    const profile = {
      fullname: emptyToNull(fullname.val()),
      phone: emptyToNull(phone.val())
    }

    disableButton(saveGeneral, true);
    $.post({
      url: `/api/settings/profile?general`,
      contentType: 'application/json'
    }, JSON.stringify(profile)).done((profile) => {
      showProfile(profile);
      toastr.success('Perfil actualizado');
    }).fail(function () {
      toastr.warning(`Ocurrio un error al ejecutar la acción, intente nuevamente más tarde.`);
    }).always(function () {
      enableButton(saveGeneral);
    });
  }

  function getProfile() {
    $.get("/api/settings/profile", showProfile);
  }

  function showProfile(profile) {
    fullname.val(profile.fullname);
    email.val(profile.email);
    phone.val(profile.phone);

    profileName.text(profile.fullname);
  }

  init();
  return element;
})({});