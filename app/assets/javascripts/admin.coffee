$ ->
  my.initAjax()

  class MasterViewModel
    constructor: ->
      @isFirstInit = true

      @initFields = ->
        emptyServerData =
          productType: 'subject'
          subjectId: 1

        ko.mapping.fromJS emptyServerData, {}, @

        @currentVM().initFields()

      @subjectVM = new SubjectViewModel(this)
      @themeVM = new ThemeViewModel(this)
      @questionVM = new QuestionViewModel(this)

      @currentVM = ko.observable @subjectVM

      @initFields()

      @subjects = ko.observableArray()
      @themes = ko.observableArray()

      @loadSubjects = =>
        $.get '/subjects'
        .done (returnedData) =>
          @subjects.removeAll()
          for subject in returnedData
            subject.subjectId = subject.id
            @subjects.push(subject)

      @loadThemes = =>
        $.get '/themes'
        .done (returnedData) =>
          @themes.removeAll()
          for theme in returnedData
            theme.themeId = theme.id
            theme.subjectId = theme.subjectId
            @themes.push(theme)

      @loadSubjects()
      @loadThemes()

      @changeActiveTab = ->
        @loadSubjects()
        @currentVM(@themeVM)
        $('#subjTab').removeClass('active')
        $('#themeTab').addClass('active')
        $('#addTheme').addClass('active in')
        $('#addSubject').toggleClass('tab-pane fade active in', 'tab-pane fade')

      @addSubject = =>
        if !my.hasText(@currentVM().name)
          alert "Zarur maydonlarni to'ldiring"
          return

        vmDataForServer = @currentVM().getDataForServer()
        subjectName = vmDataForServer.name.trim()
        if subjectName.length < 1
          alert 'Xato nom tanlandi'
          return

        for subject in @subjects()
          if subject.name.toUpperCase() == subjectName.toUpperCase()
            alert 'Iltimos, boshqa fan kiriting. Bunday fan mavjud'
            return

        sendUrl = "/admin/add-subject"
        $.post sendUrl, JSON.stringify(subjectName)
        .done (resp) =>
          @changeActiveTab()
          console.log(resp)
        .fail (error) ->
          console.log(error)
          alert 'Something went wrong! Please try again.'

      @subjectShown = ko.pureComputed(->
        @currentVM() == @subjectVM
      , @)

      @themeShown = ko.pureComputed(->
        @currentVM() == @themeVM
      , @)

      @questionShown = ko.pureComputed(->
        @currentVM() == @questionVM
      , @)

      @productTypeSelected = (productType) ->
        if productType == 'subject'
          @currentVM(@subjectVM)
        else if productType == 'theme'
          @currentVM(@themeVM)
        else if productType == 'question'
          @currentVM(@questionVM)

        @currentVM().initFields()

      @addTheme = =>
        if !my.hasText(@currentVM().name)
          alert "Zarur maydonlarni to'ldiring"
          return

        vmDataForServer = @currentVM().getDataForServer()
        ownDataForServer = ko.mapping.toJS @
        themeName = vmDataForServer.name.trim()
        vmDataForServer.subjectId = ownDataForServer.subjectId
        if themeName .length < 1
          alert 'Xato nom tanlandi'
          return

        sendUrl = "/admin/add-theme"
        $.post sendUrl, JSON.stringify(vmDataForServer)
        .done (resp) =>
#          @changeActiveTab()
          alert(resp)
        .fail (error) ->
          console.log(error)
          alert 'Something went wrong! Please try again.'


  class SubjectViewModel
    constructor: (parentVM) ->
      @parentVM = parentVM

      @initFields = ->
        emptyServerData =
          name: ''

        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

      @getDataForServer = ->
        dataForServer = ko.mapping.toJS @
        dataForServer

  class ThemeViewModel
    constructor: (parentVM) ->
      @parentVM = parentVM

      @initFields = ->
        emptyServerData =
          subjectId: ''
          name: ''

        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()

      @getDataForServer = ->
        dataForServer = ko.mapping.toJS @
        dataForServer

  class QuestionViewModel
    constructor: (parentVM) ->
      @parentVM = parentVM

      @initFields = ->
        emptyServerData =
          subjectId: ''
          theme: ''
          difficulty: ''
          question: ''
          ansA: ''
          ansB: ''
          ansC: ''
          ansD: ''
          rAns: ''

        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()


      @getDataForServer = ->
        dataForServer = ko.mapping.toJS @
        dataForServer

  ko.applyBindings new MasterViewModel()
