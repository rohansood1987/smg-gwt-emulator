// some random tests that ServerV1 should support for Developer
describe("Developer tests for ServerV1 - ", function(){

	var gameApiUrl = "http://1.smg-server.appspot.com/games";
	var settings = '{"accessSignature":"....", "gameId":"....", playerIdOfGameDeveloper:"....", url: "....", name: "...."}';
	var parameters = '{"accessSignature":"....", "gameId":"....", playerIdOfGameDeveloper:"....", parameters:"{width:"...", height:"...", minPlayers:"...", maxPlayers:"..."}"}';
	var enablePassPlay = '{"accessSignature":"....", "gameId":"....","passplay": "true"}';
	var setPerfectGameInfo = '{"accessSignature":"....", "gameId":"....","perfectInfo": "true"}';

	it("Add Game Settings", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(settings);
	});
	
	it("Add Game Parameters", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(parameters);
	});

	it("Enable pass and play mode in the game", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl + "/passplay", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(enablePassPlay);
	});
	
	it("Set game as perfect information game", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl + "/perfectInfo", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(setPerfectGameInfo);
	});

});