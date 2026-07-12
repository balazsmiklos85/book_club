# frozen_string_literal: true

require 'spec_helper'

RSpec.describe BookClub::Operations::Register do
  subject(:register_operation) { described_class.new(users:, emails:, logger:) }

  include_context 'with register operation'
  include_context 'with register user fixtures'

  describe '#call' do
    let(:valid_params) do
      {
        name: 'Alice',
        email: 'alice@example.com',
        confirm_email: 'alice@example.com',
        password: 'secure-password',
        confirm_password: 'secure-password',
        external_id: 123
      }
    end

    context 'when all parameters are valid' do
      it 'registers the user successfully' do
        result = register_operation.call(valid_params)

        expect(result.success?).to be(true)
      end

      it 'downcases the email before persisting' do
        register_operation.call(valid_params.merge(email: 'Alice@Example.COM', confirm_email: 'Alice@Example.COM'))

        expect(users).to have_received(:insert).with(
          hash_including(name: 'Alice', is_admin: false, external_id: 123)
        )
      end

      it 'creates a password record for the user using the external_id' do
        register_operation.call(valid_params)

        expect(users).to have_received(:user_passwords)
        expect(password_relation).to have_received(:command).with(:create, result: :one)
        expect(password_command).to have_received(:call).with(
          hash_including(user_id:, password_hash: be_a(String))
        )
      end

      it 'creates an email record for the user' do
        register_operation.call(valid_params)

        expect(emails).to have_received(:insert).with('alice@example.com', user_id)
      end
    end

    context 'when passwords do not match' do
      it 'returns a failure with password mismatch errors' do
        result = register_operation.call(valid_params.merge(confirm_password: 'different-password'))

        expect(result.failure?).to be(true)
        expect(result.failure[:password]).to include('does not match confirmation')
      end

      it 'does not attempt to create a user' do
        register_operation.call(valid_params.merge(confirm_password: 'different-password'))

        expect(users).not_to have_received(:insert)
      end
    end

    context 'when emails do not match' do
      it 'returns a failure with email mismatch errors' do
        result = register_operation.call(valid_params.merge(confirm_email: 'different@example.com'))

        expect(result.failure?).to be(true)
        expect(result.failure[:email]).to include('does not match confirmation')
      end

      it 'does not attempt to create a user' do
        register_operation.call(valid_params.merge(confirm_email: 'different@example.com'))

        expect(users).not_to have_received(:insert)
      end
    end

    context 'when user creation fails at the database level' do
      before { allow(users).to receive(:insert) { raise StandardError, 'Database connection lost' } }

      it 'returns a failure with :user_creation_failed key' do
        result = register_operation.call(valid_params)

        expect(result.failure?).to be(true)
        expect(result.failure).to eq(:user_creation_failed)
      end

      it 'logs the error' do
        register_operation.call(valid_params)

        expect(logger).to have_received(:error).with(include('Failed to create user'))
      end

      it 'does not attempt to create a password' do
        register_operation.call(valid_params)

        expect(users).not_to have_received(:user_passwords)
      end

      it 'does not attempt to create an email' do
        register_operation.call(valid_params)

        expect(emails).not_to have_received(:insert)
      end
    end

    context 'when password creation fails at the database level' do
      before { allow(password_command).to receive(:call) { raise StandardError, 'Constraint violation' } }

      it 'returns a failure with :password_creation_failed key' do
        result = register_operation.call(valid_params)

        expect(result.failure?).to be(true)
        expect(result.failure).to eq(:password_creation_failed)
      end

      it 'logs the error' do
        register_operation.call(valid_params)

        expect(logger).to have_received(:error).with(include('Failed to create password'))
      end
    end
  end
end
