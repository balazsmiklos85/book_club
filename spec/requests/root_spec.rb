# frozen_string_literal: true

RSpec.describe 'Root', type: :request do
  it 'redirects to login when unauthenticated' do
    get '/'

    expect(last_response.status).to eq(303)
    expect(last_response.headers['Location']).to eq('/login')
  end
end
