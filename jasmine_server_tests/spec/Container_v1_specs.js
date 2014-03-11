// some random tests that ServerV1 should support for Container
describe("Container tests for ServerV1 - ", function(){

	var matchApiUrl = "http://1.smg-server.appspot.com/matches";
	var startMatch = '{"accessSignature": "....", "playerIds": "[42,43]", "gameId": "1"}';
	var makeMove = '{"accessSignature": "....", "operation":["...."]}';
	
	it("Start a new game/match", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);				
		};
		request.open("PUT", matchApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(startMatch);
	});

	it("Make a move", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);
			expect(request.response.has("gameState")).toBe(true);						
		};
		request.open("PUT", matchApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(makeMove);
	});

	it("Get game/match info", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);
			expect(request.response.has("gameState")).toBe(true);
			expect(request.response.has("playerIds")).toBe(true);					
		};
		request.open("GET", matchApiUrl + "?matchId={matchId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
});