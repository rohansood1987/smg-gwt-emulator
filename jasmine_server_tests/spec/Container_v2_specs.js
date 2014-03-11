// some random tests that ServerV2 should support for Container
describe("Container tests for ServerV2 - ", function(){

	var matchApiUrl = "http://2.smg-server.appspot.com/matches";
	var quitMatch = '{"accessSignature": "....", "matchId":"....", playerId:"...."}';
	var syncMatch = '{"accessSignature": "....", "matchId":"....", playerId:"...."}';
	
	it("Quit a game/match", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("quitPlayerId")).toBe(true);				
		};
		request.open("PUT", matchApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(quitMatch);
	});

	it("Play synchronosuly", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", matchApiUrl + "/syncPlay", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(syncMatch);
	});

	it("Get Remaining Time in my turn", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("time")).toBe(true);
		};
		request.open("GET", matchApiUrl + "/info/timeleft?accessSignature=....?matchId={matchId}?playerId={playerId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

	it("Get game/match info to viewers", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);
			expect(request.response.has("gameState")).toBe(true);
			expect(request.response.has("playerIds")).toBe(true);					
		};
		request.open("GET", matchApiUrl + "/info?accessSignature=....?matchId={matchId}?viewer=true", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
});