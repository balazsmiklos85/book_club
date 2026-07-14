# frozen_string_literal: true

require 'spec_helper'

RSpec.describe BookClub::Operations::Login do
  subject(:login_operation) { described_class.new(emails: email_repo, logger: logger) }

  let(:email_repo) { instance_double(BookClub::Repos::Emails) }
  let(:logger) { instance_double(Logger, info: nil, error: nil) }

  describe '#call' do
    let(:password_hash) { instance_double(BookClub::Structs::UserPassword) }
    let(:user_with_password) do
      BookClub::Structs::User.new(
        external_id: 123,
        user_passwords: [password_hash]
      )
    end
    let(:email_with_user) do
      BookClub::Structs::Email.new(
        email_address: 'user@example.com',
        user: user_with_password
      )
    end

    context 'when an existing user provides correct credentials' do
      it 'allows the login to succeed and returns the user external_id' do
        allow(email_repo).to receive(:find_with_user_and_password).and_return(email_with_user)
        allow(password_hash).to receive(:valid?).with('correct-password').and_return(true)

        result = login_operation.call(email: 'user@example.com', password: 'correct-password')

        expect(result.success?).to be(true)
      end
    end

    context 'when an existing user provides incorrect credentials' do
      it 'denies the login attempt' do
        allow(password_hash).to receive(:valid?).and_return(false)
        allow(email_repo).to receive(:find_with_user_and_password).and_return(email_with_user)

        result = login_operation.call(email: 'user@example.com', password: 'wrong-password')

        expect(result.failure?).to be(true)
      end
    end

    context 'when a non-existing user attempts to log in' do
      it 'denies the login attempt regardless of the password provided' do
        allow(email_repo).to receive(:find_with_user_and_password).and_return(nil)

        result = login_operation.call(email: 'nonexistent@example.com', password: 'any-password')

        expect(result.failure?).to be(true)
      end
    end

    context 'when an unexpected error occurs during user lookup' do
      it 'denies the login attempt' do
        allow(email_repo).to receive(:find_with_user_and_password) { raise StandardError, 'Database connection failed' }

        result = login_operation.call(email: 'user@example.com', password: 'any-password')

        expect(result.failure?).to be(true)
      end
    end

    context 'when an unexpected error occurs during password verification' do
      it 'denies the login attempt' do
        allow(email_repo).to receive(:find_with_user_and_password).and_return(email_with_user)
        allow(password_hash).to receive(:valid?) { raise StandardError, 'Hash comparison failed' }

        result = login_operation.call(email: 'user@example.com', password: 'any-password')

        expect(result.failure?).to be(true)
      end
    end
  end
end
