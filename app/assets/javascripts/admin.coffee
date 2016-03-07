$ ->
  my.initAjax()

  class MasterViewModel
    constructor: ->
      @isFirstInit = true

      @initFields = ->
        emptyServerData =
          productType: 'uloc'
          messageType: null
          phonePart1: ''
          phonePart2: ''
          phonePart3: ''
          confirmPhonePart1: ''
          confirmPhonePart2: ''
          confirmPhonePart3: ''
          email: ''
          confirmEmail: ''

        if !@isFirstInit
          emptyServerData.productType = @productType()

        ko.mapping.fromJS emptyServerData, {}, @

        @currentVM().initFields()
        @isFirstInit = false

      @subjectVM = new SubjectViewModel(this)
      @themeVM = new ThemeViewModel(this)

      @currentVM = ko.observable @subjectVM

      @initFields()

      @smsFailedShown = ko.observable false
      @smsSucceededShown = ko.observable false
      @emailFailedShown = ko.observable false
      @emailSucceededShown = ko.observable false

      @subjectShown = ko.pureComputed(->
        @currentVM() == @subjectVM
      , @)

      @themeShown = ko.pureComputed(->
        @currentVM() == @themeVM
      , @)


      @productTypeSelected = ->
        if @productType() == 'uloc'
          @currentVM(@subjectVM)
        else if @productType() == 'loan'
          @currentVM(@themeVM)

        @currentVM().initFields()


      @readyForSending = ko.pureComputed(->
        @messageType() != null and
          @email() == @confirmEmail() and
          @phonePart1() == @confirmPhonePart1() and
          @phonePart2() == @confirmPhonePart2() and
          @phonePart3() == @confirmPhonePart3() and
          (!@needPhoneNumber() or my.isValidPhone(@getPhoneNumber())) and
          (!@needEmail() or my.isValidEmail(@email()))
      , @)

      @needPhoneNumber = ko.pureComputed(->
        @messageType() == "sms"
      , @)

      @needEmail = ko.pureComputed(->
        @messageType() == "email"
      , @)

      @sendMessage = ->
        if !my.hasText(@currentVM().fullName())
          if @language() == 'fr'
            window.alert "Les données suivantes sont invalides:\n- Nom du client"
          else
            window.alert "The following fields values are invalid:\n- Customer's name"

          return

        if @currentVM() == @themeVM && @currentVM().isPayoutClose()
          isClosePayoutExist = false
          for obj in @currentVM().payouts()
            if obj.isClose()
              isClosePayoutExist = true
          if !isClosePayoutExist
            if @language() == 'fr'
              window.alert "You've selected 'Payout & Close' but no payouts are marked as 'close'."
            else
              window.alert "You've selected 'Payout & Close' but no payouts are marked as 'close'."

            return

        vmDataForServer = @currentVM().getDataForServer()
        sendUrl = "/TD_disclosure/assist/#{@currentVM().getSendActionName()}"

        ownDataForServer = ko.mapping.toJS @
        dataForServer = _.extend {}, vmDataForServer, ownDataForServer

        $.post sendUrl, JSON.stringify(dataForServer)
        .done (returnedData) =>
          if returnedData.isSent
            my.showSendingSucceeded @
            @initFields()
          else
            window.alert "Sending message failed.\n\nReason: #{returnedData.failReason}"
            my.showSendingFailed @
        .fail (returnedData) =>
          my.showSendingFailed @


  class SubjectViewModel
    constructor: (parentVM) ->
      @parentVM = parentVM

      @initFields = ->
        emptyServerData =
          name: ''

        ko.mapping.fromJS emptyServerData, {}, @

      @initFields()



      @getSendActionName = ->
        'sendUloc'

      @getDataForServer = ->
        dataForServer = ko.mapping.toJS @
        dataForServer


  class ThemeViewModel
    constructor: (parentVM) ->
      @parentVM = parentVM

      @initFields = ->
        emptyServerData =
          subjectName: ''
          name: ''

        ko.mapping.fromJS emptyServerData, {}, @

      @currentStep = ko.observable 1

      @initFields()

      @toStep1 = ->
        @currentStep(1)

      @toStep2 = ->
        if !@isCampaign()
          if @parentVM.language() == 'fr'
            window.alert "Please Select Campaign"
          else
            window.alert "Please Select Campaign"

          return

        @currentStep(2)

      @isOnStep1 = ko.pureComputed(->
        @currentStep() == 1
      , @)

      @isOnStep2 = ko.pureComputed(->
        @currentStep() == 2
      , @)

      @isPhoneEmailPartShown = ko.pureComputed(->
        @isOnStep2()
      , @)

      @isFixedInterestRateType = ko.pureComputed(->
        @interestRate.interestRateType() == 'fixed'
      , @)

      @interestRateTypeChanged = ->
        @interestRate.fixedInterestRate(null)
        @interestRate.primeInterestRate(null)
        @interestRate.varianceInterestRate(null)

      @isTdctPaymentType = ko.pureComputed(->
        @paymentType() == 'tdct'
      , @)

      @paymentTypeChanged = ->
        @paymentAccount(null)
        @finInst.instNumber(null)
        @finInst.branch(null)
        @finInst.accountNumber(null)

      @totalVariableRate = ko.pureComputed(->
        if @isFixedInterestRateType()
          ''
        else
          totalRate = parseFloat(@interestRate.primeInterestRate()) + parseFloat(@interestRate.varianceInterestRate())
          if isNaN(totalRate)
            ''
          else
            totalRate.toFixed(2)
      , @)

      @isPayoutsTablesShown = ko.pureComputed(->
        @payoutType() == 'payout' || @payoutType() == 'payoutClose'
      , @)

      @issueDateChanged = ->
        firstP = if my.hasText(@loanDates.issueDate())
          moment(@loanDates.issueDate(), 'MM/DD/YYYY').add(1, 'months').format('MM/DD/YYYY')
        else
          ''
        @loanDates.firstPaymentDate(firstP)
      #        $('#firstPaymentDate').datepicker("update", firstP)


  ko.applyBindings new MasterViewModel()
