# frozen_string_literal: true

require 'spec_helper'

RSpec.describe 'User Registration', type: :feature do
  before { Capybara.app = Hanami.app }

  let(:valid_params) do
    {
      name: 'Alice',
      email: 'alice@example.com',
      confirm_email: 'alice@example.com',
      password: 'secure-password',
      confirm_password: 'secure-password',
      external_id: '123'
    }
  end

  context 'successful registration' do
    it 'redirects to root with session set', :aggregate_failures do
      visit '/register'

      fill_in('name', with: valid_params[:name])
      fill_in('email', with: valid_params[:email])
      fill_in('confirm_email', with: valid_params[:confirm_email])
      fill_in('password', with: valid_params[:password])
      fill_in('confirm_password', with: valid_params[:confirm_password])
      fill_in('external_id', with: valid_params[:external_id])

      click_button('Register')

      expect(page).to have_current_path('/')
    end
  end

  context 'with non-matching confirm_email' do
    it 'redirects to register page with flash error' do
      visit '/register'

      fill_in('name', with: valid_params[:name])
      fill_in('email', with: valid_params[:email])
      fill_in('confirm_email', with: 'different@example.com')
      fill_in('password', with: valid_params[:password])
      fill_in('confirm_password', with: valid_params[:confirm_password])
      fill_in('external_id', with: valid_params[:external_id])
      click_button('Register')

      expect(page).to have_current_path('/register')
      expect(page).to have_selector('div[style="color: red;"]', text: 'Registration failed')
    end
  end

  context 'GET /register' do
    it 'returns 200 status' do
      visit '/register'

      expect(page.status_code).to eq(200)
    end

    it 'displays the registration form' do
      visit '/register'

      expect(page).to have_selector('h1', text: 'Register')
      expect(page).to have_field('name')
      expect(page).to have_field('email')
      expect(page).to have_field('confirm_email')
      expect(page).to have_field('password')
      expect(page).to have_field('confirm_password')
      expect(page).to have_field('external_id')
    end
  end
end
