var playerApiUrl = 'http://1.smg-server.appspot.com/players'

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


describe("Player V1", function() {

	it("Connectivity test", function() {
		var request = new XMLHttpRequest();
		request.onload = function() {
			expect(request.status).toEqual(200)
		}
		request.open("GET", "http://www.google.com", true)
		request.send();
	})

	it("Add a new player", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.has("playerId")).toBe(true);
			
		}
		request.open("PUT", playerApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(addNewPlayerRequest);
	})

	it("Add an existing player", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.get("error")).toEqual(emailExistsError);
		}
		request.open("PUT", playerApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(addNewPlayerRequest);
	})

	it("Update player information", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.get("success")).toEqual(success_updatedPlayer);
		}
		request.open("PUT", playerApiUrl + "/" + playerId_correct, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updatePlayerInfoRequest);
	})

	it("Update player information with wrong Access Signature", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.get("error")).toEqual(error_wrongAccessSig);
		}
		request.open("PUT", playerApiUrl + "/" + playerId_correct, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updatePlayerInfoWrongSigRequest);
	})

	it("Update player information with wrong PlayerID", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(request.response.get("error")).toEqual(error_wrongPlayerID);
		}
		request.open("PUT", playerApiUrl + "/" + playerId_wrong, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(updatePlayerInfoRequest);
	})
})