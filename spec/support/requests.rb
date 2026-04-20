# frozen_string_literal: true

require 'rack/test'

RSpec.shared_context 'with Rack::Test' do
  let(:app) { Hanami.app }

  def session
    last_request.session
  end

  def flash
    last_request.session[:_flash] ||= {}
  end

  # Ensure cookies persist across requests in tests
  def clear_cookies
    @cookie_jar = {}
  end
end

RSpec.configure do |config|
  config.include Rack::Test::Methods, type: :request
  config.include_context 'with Rack::Test', type: :request
end
