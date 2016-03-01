$ ->

  my.initAjax()

  birthDateFormat = 'MM/DD/YYYY'
  startDatetime = '01/01/1995'

  ko.bindingHandlers.htmlWithBinding =
    'init': ->
      { 'controlsDescendantBindings': true }
    'update': (element, valueAccessor, allBindings, viewModel, bindingContext) ->
      element.innerHTML = valueAccessor()
      ko.applyBindingsToDescendants bindingContext, element

  handleError = (error) ->
    alert 'Something went wrong! Please try again.'

  emptyServerData =
    firstName: ""
    lastName: ""
    email: ""
    birthDate: ""
    password: ""
    confirmPassword: ""
    gender: "male"

  initDatePicker = (selector, defaultDate, format) ->
    $el = $(selector)
    $el.on('dp.hide', () ->
      $el.find('input').change()
    )

    $el.datetimepicker
      format: format
      useCurrent: no
      defaultDate: defaultDate

  initDatePicker('#birthDate', startDatetime, birthDateFormat)

  class RegistrationViewModel
    constructor: ->
      @initFields = ->
        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

      @gender = ko.observable('')

      @onSubmit = (=>
        dataForReport = ko.mapping.toJS @
        console.log(dataForReport)
        $.post('/user/registration', JSON.stringify(dataForReport))
        .fail handleError
        .done (returnedData) =>
          console.log(returnedData)
      ).bind(this)

  ko.applyBindings  new RegistrationViewModel()
