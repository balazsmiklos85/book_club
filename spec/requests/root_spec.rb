# frozen_string_literal: true

RSpec.describe 'Root', type: :request do
  it 'routes to leaderboard index' do
    get '/'

    expect(last_response.status).to eq(200)
  end
end
