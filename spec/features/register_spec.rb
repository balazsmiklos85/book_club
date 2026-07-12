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

  def fill_registration_form(params = valid_params)
    params.each do |field, value|
      fill_in(field, with: value)
    end
  end

  context 'with valid parameters' do
    it 'redirects to root with session set', :aggregate_failures do
      visit '/register'
      fill_registration_form
      click_button('Register')

      expect(page).to have_current_path('/')
    end
  end

  context 'with non-matching confirm_email' do
    it 'redirects to register page with flash error' do
      visit '/register'
      fill_registration_form(valid_params.merge(confirm_email: 'different@example.com'))
      click_button('Register')

      expect(page).to have_current_path('/register')
      expect(page).to have_selector('div[style="color: red;"]', text: 'Registration failed')
    end
  end

  context 'when viewing the registration page' do
    it 'returns 200 status' do
      visit '/register'

      expect(page.status_code).to eq(200)
    end

    it 'displays the registration form' do
      visit '/register'

      expect(page).to have_selector('h1', text: 'Register')
      selector = %w[name email confirm_email password confirm_password external_id]
                 .map { |field| "input[name=\"#{field}\"]" }
                 .join ', '
      expect(page).to have_css(selector)
    end
  end
end
