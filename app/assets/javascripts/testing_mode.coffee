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

  emptyServerData =
    subjectId: ""
    theme: ""
    difficult: ""
    firstBlock: ""
    secondBlock: ""
    thirdBlock: ""

  class TestingModeViewModel
    constructor: ->
      @initFields = ->
        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

      @subjects = ko.observableArray([])
      @themes = ko.observableArray([])
      @selectedSubject = ko.observable()
      @selectedTheme = ko.observable()

      @onSubjectChange = =>
        $.get "/themes/#{@selectedSubject().subjectId}"
        .done (returnedData) =>
          @themes.removeAll()
          for theme in returnedData
            console.log(theme)
            theme.themeId = theme.id
            theme.subjectId = theme.subjectId
            @themes.push(theme)


      $.get '/subjects'
      .done (returnedData) =>
        @subjects.removeAll()
        for subject in returnedData
          subject.subjectId = subject.id
          @subjects.push(subject)

  ko.applyBindings  new TestingModeViewModel()

