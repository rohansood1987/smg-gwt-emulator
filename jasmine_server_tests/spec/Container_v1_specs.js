// some random tests that ServerV1 should support for Container
describe("Container tests for ServerV1 - ", function(){

	var matchApiUrl = "http://1.smg-server.appspot.com/matches";
	var startMatch = '{"accessSignature": "....", "playerIds": "[42,43]", "gameId": "1"}';
	
	it("Start a new game/match", function(){
		var request = new XMLHttpRequest();
		request.onload = function() {
			expect(request.status).toEqual(200)
		};
		request.open("PUT", matchApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(startMatch);
		expect(request.response.has("matchId")).toBe(true);
	});
});