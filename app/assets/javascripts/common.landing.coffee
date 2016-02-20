$ ->
  $subpages = $('.subpage')

  $contactUs = $('.contact-us-subpage')
  $faq = $('.faq-subpage')

  $('.contact-us-link').click ->
    $subpages.hide()
    $contactUs.show()
    true

  $('.faq-link').click ->
    $subpages.hide()
    $faq.show()
    true
