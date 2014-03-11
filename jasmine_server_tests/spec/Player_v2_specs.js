var playerApiUrl = 'http://2.smg-server.appspot.com/players'
var gameApiUrl = 'http://2.smg-server.appspot.com/games'

var error_emailExists = "EMAIL_EXISTS"
var success_updatedPlayer = "UPDATED_PLAYER"
var error_wrongAccessSig = "WRONG ACCESS SIGNATURE"
var error_wrongPlayerID = "WRONG PLAYER ID"

var playerId_correct = "42";
var playerId_wrong = "50";

var addNewPlayerRequest = '{email":"newPlayer@gmail.com","password":"1234","firstName":"New",lastName:"Player"}'
var addExistingPlayerRequest = '{"email":"existingPlayer@gmail.com","password":"1234","firstName":"Existing",lastName:"Player"}'

var updatePlayerInfoRequest = '{"accessSignature":"correct","email":"newPlayer123@gmail.com","password":"12345","firstName":"New1",lastName:"Player1"}'
var updatePlayerInfoWrongSigRequest = '{"accessSignature":"wrong","email":"newPlayer123@gmail.com","password":"12345","firstName":"New1",lastName:"Player1"}'


describe("Player tests for ServerV2", function() {

	it("Get Game History of player", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("gameHistoryList")).toBe(true);
			
		}
		request.open("GET", playerApiUrl + "/" + playerId_correct + "/GameHistory", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	})

	it("Get a player's profile", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("tokens")).toBe(true);
			
		}
		request.open("GET", playerApiUrl + "/" + playerId_correct, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	})

	it("Get viewable games for a player", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("viewableGames")).toBe(true);
			
		}
		request.open("GET", gameApiUrl + "/" + playerId_correct +  "/viewableGames", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	})
})