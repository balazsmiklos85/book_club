# frozen_string_literal: true

module TestFactories
  # Creates a user with the given email and password in the test database.
  #
  # @param email [String] the user's email address
  # @param password [String] the user's password (defaults to 'password123')
  # @return [OpenStruct] an object containing external_id, email, and other user data
  def self.create_user(email:, password: 'password123', name: 'Test User')
    container = Hanami.app['db.rom']
    external_id = SecureRandom.uuid

    # Insert user
    users = container.relation(:users)
    users.insert(external_id: external_id, name: name)

    # Insert email
    emails = container.relation(:emails)
    emails.insert(user_external_id: external_id, email_address: email)

    # Insert password hash (using bcrypt-style hashing for testing)
    user_passwords = container.relation(:user_passwords)
    password_hash = BCrypt::Password.create(password).to_s
    user_passwords.insert(
      user_external_id: external_id,
      user_password_hash: password_hash,
      created_at: Time.current
    )

    OpenStruct.new(external_id: external_id, email: email)
  end
end
