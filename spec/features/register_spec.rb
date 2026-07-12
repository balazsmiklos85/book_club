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

  context 'when viewing the registration page' do
    it 'displays the form and redirects to root on successful registration', :aggregate_failures do
      visit '/register'

      expect(page.status_code).to eq(200)
      expect(page).to have_selector('h1', text: 'Register')
      selector = %w[name email confirm_email password confirm_password external_id]
                 .map { |field| "input[name=\"#{field}\"]" }
                 .join ', '
      expect(page).to have_css(selector)

      fill_registration_form
      click_button('Register')

      expect(page).to have_current_path('/')
    end

    it 'redirects to register page with flash error on non-matching confirm_email' do
      visit '/register'
      fill_registration_form(valid_params.merge(confirm_email: 'different@example.com'))
      click_button('Register')

      expect(page).to have_current_path('/register')
      expect(page).to have_selector('div[style="color: red;"]', text: 'Registration failed')
    end
  end
end
