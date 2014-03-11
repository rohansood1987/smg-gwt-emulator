var playerApiUrl = 'http://3.smg-server.appspot.com/players'
var gameApiUrl = 'http://3.smg-server.appspot.com/games'

var error_emailExists = "EMAIL_EXISTS"
var success_updatedPlayer = "UPDATED_PLAYER"
var error_wrongAccessSig = "WRONG ACCESS SIGNATURE"
var error_wrongPlayerID = "WRONG PLAYER ID"

var playerId = "42";
var playerId_wrong = "50";
var playerId_friend = "43";
var playerId_friend_wrong = "51";


describe("Player tests for ServerV3", function() {

	it("Send a friend request", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("success")).toBe(true);
			
		}
		request.open("PUT", playerApiUrl + "/" + playerId + "/FriendRequest", true);//
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send('{"friendId":"' + playerId_friend + '","action":"ADD"}');
	})

	it("Remove an existing friend", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("success")).toBe(true);
			
		}
		request.open("PUT", playerApiUrl + "/" + playerId + "/FriendRequest", true);//
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send('{"friendId":"' + playerId_friend + '","action":"REMOVE"}');
	})

	it("Block a friend", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("success")).toBe(true);
			
		}
		request.open("PUT", playerApiUrl + "/" + playerId + "/FriendRequest", true);//
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send('{"friendId":"' + playerId_friend + '","action":"BLCOK"}');
	})

	it("Un-block a friend", function() {
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.status).toEqual(200);
			var response = request.response;
			
			// Check return params
			expect(response.has("success")).toBe(true);
			
		}
		request.open("PUT", playerApiUrl + "/" + playerId + "/FriendRequest", true);//
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send('{"friendId":"' + playerId_friend + '","action":"UNBLOCK"}');
	})
})