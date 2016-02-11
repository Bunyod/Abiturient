if !window.console
  window.console =
    log: ->
      # nothing

root = exports ? this

root.my =
  growlTimeoutSeconds: 5

  showGrowl: (observableBoolean) ->
    observableBoolean true
    setTimeout (->
      observableBoolean false
    ), my.growlTimeoutSeconds * 1000


  initAjax: ->
    $.ajaxSetup
      type: 'POST'
      contentType: "application/json"
      dataType: 'json'
      headers:
        'Cache-Control': 'no-cache'
        'Pragma': 'no-cache'

  regexpAlpha:
    /[a-z]/i

  hasText: (s) ->
    !_.isEmpty(_.trim(s))

  isDigits: (s, digitsCount) ->
    (new RegExp('^\\d{' + digitsCount + '}$')).test s

  isValidPhone: (phoneNumber) ->
    /^\d{10}$/.test phoneNumber

  isValidEmail: (email) ->
    re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    re.test email

  isValidInt: (s) ->
    /^\d{1,}$/.test s

  isValidDecimal2: (s) ->
    /^\d{1,}\.\d{2}$/.test s

  convertToFloat: (observables) ->
    for observable in observables
      if !my.hasText(observable())
        observable null
      else
        observable parseFloat(observable())
