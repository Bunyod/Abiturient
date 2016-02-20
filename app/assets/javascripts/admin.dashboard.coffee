$ ->
  my.initAjax()
  startTime = "00:00"
  endTime = "23:59"
  startDatetime = moment(startTime, 'hh:mm A').format('YYYY-MM-DD hh:mm A')
  endDatetime = moment(endTime, 'hh:mm A').format('YYYY-MM-DD hh:mm A')

  $('ul.nav li.dropdown').hover (->
    $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeIn 100
  ), ->
    $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeOut 100

  $(document).on 'click', '.dropdown-menu li a', ->
    $('#datebox').val $(this).html()

  logout = ->
    window.alert("Your session has been expired!\nPlease log in.")
    window.location.href = '/logout'

  handleError = (error) ->
    if error.status is 401
      logout()
    else
      alert 'Something went wrong! Please try again.'

  emptyServerData =
    startDate: ""
    endDate: ""
    reportType: "byDate"

  ko.bindingHandlers.datetimepicker =
    init: (element, valueAccessor, allBindings) ->
      options =
        format: 'YYYY-MM-DD hh:mm A'
        defaultDate: ko.unwrap(valueAccessor())
      ko.utils.extend options, allBindings.dateTimePickerOptions

      dtp = $(element).datetimepicker(options)

      dtpFunction = (evntObj) ->
        observable = valueAccessor()
        if evntObj.timeStamp != undefined
          picker = $(this).data('DateTimePicker')
          d = picker.date
          observable d.format(options.format)

      dtp.on 'dp.change', dtpFunction

      # we need it because if user selects the current date 'dp.change' is not fired
      dtp.on 'dp.hide', dtpFunction

    update: (element, valueAccessor) ->
      value = ko.unwrap(valueAccessor())
      $(element).datetimepicker 'date', value or ''

  convertIntToDateTime = (intDate)->
    moment(intDate).format('MMM DD, YYYY HH:mm:ss')

  $('#dateFrom').datetimepicker({
    format: 'YYYY-MM-DD hh:mm A',
    useCurrent:false,
    defaultDate:startDatetime
  })
  $('#dateTo').datetimepicker({
    format: 'YYYY-MM-DD hh:mm A',
    useCurrent:false,
    defaultDate:endDatetime
  })

  class ReportViewModel
    constructor: ->
      @initFields = ->
        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

  ko.applyBindings  new ReportViewModel()
