# frozen_string_literal: true

RSpec.shared_context 'with register operation' do
  let(:password_command) { double(call: nil) }
  let(:password_relation) { double(command: password_command) }
  let(:users) { double(insert: user_struct, user_passwords: password_relation) }
  let(:emails) { double(insert: nil) }
  let(:logger) { instance_double(Logger, info: nil, error: nil) }
end
