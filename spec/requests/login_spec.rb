# frozen_string_literal: true

require 'spec_helper'

RSpec.describe 'Login', type: :request do
  describe 'GET /login' do
    it 'returns 200 status' do
      get '/login'
      expect(last_response.status).to eq(200)
    end

    it 'displays the login form' do
      get '/login'
      expect(last_response.body).to include('login')
    end

    it 'renders the login form template' do
      get '/login'
      expect(last_response.body).to include('Email')
    end

    it 'renders password field' do
      get '/login'
      expect(last_response.body).to include('Password')
    end
  end
end
