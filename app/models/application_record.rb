# frozen_string_literal: true

# Base class for all models. This is an abstract class, and should not be instantiated.
class ApplicationRecord < ActiveRecord::Base
  primary_abstract_class
end
