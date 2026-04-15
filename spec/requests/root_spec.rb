# frozen_string_literal: true

RSpec.describe 'Root', type: :request do
  it 'redirects with 303 status when unauthenticated' do
    get '/'

    expect(last_response.status).to eq(303)
  end

  it 'redirects to login page when unauthenticated' do
    get '/'

    expect(last_response.headers['Location']).to eq('/login')
  end
end
