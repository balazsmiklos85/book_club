# frozen_string_literal: true

require 'spec_helper'

RSpec.describe BookClub::Types::EmailAddress do
  subject(:email_address_type) { described_class }

  describe '[]' do
    it 'downcases the email address' do
      expect(email_address_type['Alice@Example.COM']).to eq('alice@example.com')
    end

    it 'returns already-lowercase email unchanged' do
      expect(email_address_type['alice@example.com']).to eq('alice@example.com')
    end

    it 'raises on non-string input' do
      expect { email_address_type[123] }.to raise_error(Dry::Types::CoercionError)
    end
  end
end
