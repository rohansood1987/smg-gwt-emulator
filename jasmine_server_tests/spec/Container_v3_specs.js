// some random tests that ServerV3 should support for Container
describe("Container tests for ServerV3 - ", function(){

	var matchApiUrl = "http://3.smg-server.appspot.com/matches";
	
	it("Save a match", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);				
		};
		request.open("GET", matchApiUrl + "/save?accessSignature=....?matchId={matchId}?playerId={playerId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

	it("Load a match", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("matchId")).toBe(true);
		};
		request.open("GET", matchApiUrl + "/load?accessSignature=....?matchId={matchId}?playerId={playerId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

	it("Get match view on iphone", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);
		};
		request.open("GET", matchApiUrl + "/view?accessSignature=....?iphone=true?matchId={matchId}?playerId={playerId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

	it("Enable pass & play mode", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);
		};
		request.open("GET", matchApiUrl + "/pass-play?accessSignature=....?matchId={matchId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
});