$ ->
  my.initAjax()

  ko.bindingHandlers.htmlWithBinding =
    'init': ->
      { 'controlsDescendantBindings': true }
    'update': (element, valueAccessor, allBindings, viewModel, bindingContext) ->
      element.innerHTML = valueAccessor()
      ko.applyBindingsToDescendants bindingContext, element

  logout = ->
    window.alert("Your session has been expired!\nPlease log in.")
    window.location.href = '/logout'

  handleError = (error) ->
    if error.status is 401
      logout()
    else
      alert 'Something went wrong! Please try again.'

  emptyServerData =
    spacialization: ""

#  convertIntToDateTime = (intDate)->
#    moment(intDate).format('MMM DD, YYYY HH:mm:ss')

  class ReportViewModel
    constructor: ->
      @initFields = ->
        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

      @questions = ko.observableArray([])
      @currentQuestion = ko.observable()


      $.get('/quizes')
        .done (returnedData) =>
          @questions.removeAll()
          for value in returnedData
            @questions.push(value)
        .fail (returnedData) =>
          console.log('error loading')

      @getQuestion = (questionNumber) =>
        @currentQuestion(@questions()[questionNumber])



  ko.applyBindings  new ReportViewModel()

