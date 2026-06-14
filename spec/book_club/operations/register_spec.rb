# frozen_string_literal: true

require 'spec_helper'

RSpec.describe BookClub::Operations::Register do
  subject(:register_operation) { described_class.new(users:, logger:) }

  let(:password_relation) { double(insert: nil) }
  let(:user_id) { '550e8400-e29b-41d4-a716-446655440000' }
  let(:users) { double(insert: user_id, user_passwords: password_relation) }
  let(:logger) { instance_double(Logger, info: nil, error: nil) }

  describe '#call' do
    let(:valid_params) do
      {
        name: 'Alice',
        email: 'alice@example.com',
        password: 'secure-password',
        confirm_password: 'secure-password',
        external_id: 123
      }
    end

    context 'when all parameters are valid' do
      it 'registers the user successfully' do
        result = register_operation.call(**valid_params)

        expect(result.success?).to be(true)
      end

      it 'downcases the email before persisting' do
        register_operation.call(**valid_params.merge(email: 'Alice@Example.COM'))

        expect(users).to have_received(:insert).with(
          hash_including(name: 'Alice', is_admin: false, external_id: 123)
        )
      end

      it 'creates a password record for the user using the external_id' do
        register_operation.call(**valid_params)

        expect(users).to have_received(:user_passwords).with(user_id)
        expect(password_relation).to have_received(:insert).with(
          hash_including(password_hash: be_a(String), salt: '', hash_algorithm: 'bcrypt')
        )
      end
    end

    context 'when passwords do not match' do
      it 'returns a failure with :password_mismatch key' do
        result = register_operation.call(**valid_params.merge(confirm_password: 'different-password'))

        expect(result.failure?).to be(true)
        expect(result.failure).to eq(:password_mismatch)
      end

      it 'does not attempt to create a user' do
        register_operation.call(**valid_params.merge(confirm_password: 'different-password'))

        expect(users).not_to have_received(:insert)
      end
    end

    context 'when user creation fails at the database level' do
      before { allow(users).to receive(:insert) { raise StandardError, 'Database connection lost' } }

      it 'returns a failure with :user_creation_failed key' do
        result = register_operation.call(**valid_params)

        expect(result.failure?).to be(true)
        expect(result.failure).to eq(:user_creation_failed)
      end

      it 'logs the error' do
        register_operation.call(**valid_params)

        expect(logger).to have_received(:error).with(include('Failed to create user'))
      end

      it 'does not attempt to create a password' do
        register_operation.call(**valid_params)

        expect(users).not_to have_received(:user_passwords)
      end
    end

    context 'when password creation fails at the database level' do
      before { allow(password_relation).to receive(:insert) { raise StandardError, 'Constraint violation' } }

      it 'returns a failure with :password_creation_failed key' do
        result = register_operation.call(**valid_params)

        expect(result.failure?).to be(true)
        expect(result.failure).to eq(:password_creation_failed)
      end

      it 'logs the error' do
        register_operation.call(**valid_params)

        expect(logger).to have_received(:error).with(include('Failed to create password'))
      end
    end
  end
end
