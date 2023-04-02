# frozen_string_literal: true

# Base class for all mailers. This is an abstract class, and should not be instantiated.
class ApplicationMailer < ActionMailer::Base
  default from: 'from@example.com'
  layout 'mailer'
end
