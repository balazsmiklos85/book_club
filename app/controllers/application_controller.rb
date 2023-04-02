# frozen_string_literal: true

# Base class for all controllers. This is an abstract class, and should not be instantiated.
class ApplicationController < ActionController::Base
  protect_from_forgery with: :exception
  include SessionsHelper
end
