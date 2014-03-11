var playerApiUrl = 'http://3.smg-server.appspot.com/players'
var gameApiUrl = 'http://3.smg-server.appspot.com/games'

var error_emailExists = "EMAIL_EXISTS"
var success_updatedPlayer = "UPDATED_PLAYER"
var error_wrongAccessSig = "WRONG ACCESS SIGNATURE"
var error_wrongPlayerID = "WRONG PLAYER ID"

var gameId = "1490"

var playerId_correct = "42";
var playerId_wrong = "50";

var updateGameNumberOfPlayersRequest = '{"accessSignature":"correct","parameters":{"minPlayers":2,"maxPlayers":9}"}'
var updateGameAddAIRequest = '{"accessSignature":"correct","parameters":{"hasAI":true}"}'
var updateGameMakeNonTurnBasedRequest = '{"accessSignature":"correct","parameters":{"isTurnBased":false}"}'


describe("Developer tests for ServerV3", function() {

	it("Change the number of players for a game", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.has("success")).toBe(true);
			
		}
		request.open("PUT", gameApiUrl + "/" + gameId, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updateGameNumberOfPlayersRequest);
	})

	it("Get the statistics for a game", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.has("gameStatistics")).toBe(true);
			
		}
		request.open("GET", gameApiUrl + "/" + gameId + "/Stats", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updateGameNumberOfPlayersRequest);
	})

	it("Change the game to be non-turn-based", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.has("success")).toBe(true);
			
		}
		request.open("PUT", gameApiUrl + "/" + gameId, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updateGameAddAIRequest);
	})

	it("Change the game to allow AI player", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.has("success")).toBe(true);
			
		}
		request.open("PUT", gameApiUrl + "/" + gameId, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updateGameMakeNonTurnBasedRequest);
	})

})