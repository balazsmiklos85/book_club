# frozen_string_literal: true

require 'spec_helper'

RSpec.describe 'Session Creation', type: :request do
  describe 'POST /session' do
    context 'with valid credentials' do
      let(:user) { TestFactories.create_user(email: 'valid@example.com', password: 'correct123') }

      it 'creates a session and redirects to the homepage' do
        post '/session', params: { email: 'valid@example.com', password: 'correct123' }
        expect(last_response.status).to eq(303)
        expect(last_response.headers['Location']).to eq('/')
      end

      it 'stores the user external_id in the session' do
        post '/session', params: { email: 'valid@example.com', password: 'correct123' }
        expect(session[:external_id]).not_to be_nil
      end
    end

    context 'with invalid credentials' do
      it 'redirects to the login page with an error message' do
        post '/session', params: { email: 'user@example.com', password: 'wrong-password' }
        expect(last_response.status).to eq(303)
        expect(last_response.headers['Location']).to eq('/login')
        expect(flash[:error]).to eq('Invalid email or password')
      end

      it 'does not create a session when credentials are invalid' do
        post '/session', params: { email: 'user@example.com', password: 'wrong-password' }
        expect(session[:external_id]).to be_nil
      end
    end

    context 'with non-existent user' do
      it 'redirects to the login page without revealing user existence' do
        post '/session', params: { email: 'nonexistent@example.com', password: 'any-password' }
        expect(last_response.status).to eq(303)
        expect(last_response.headers['Location']).to eq('/login')
        expect(flash[:error]).to eq('Invalid email or password')
      end

      it 'does not create a session for non-existent users' do
        post '/session', params: { email: 'nonexistent@example.com', password: 'any-password' }
        expect(session[:external_id]).to be_nil
      end
    end

    context 'with missing parameters' do
      it 'redirects to the login page when email is missing' do
        post '/session', params: { password: 'some-password' }
        expect(last_response.status).to eq(303)
        expect(session[:external_id]).to be_nil
      end

      it 'redirects to the login page when password is missing' do
        post '/session', params: { email: 'user@example.com' }
        expect(last_response.status).to eq(303)
        expect(session[:external_id]).to be_nil
      end
    end
  end
end
