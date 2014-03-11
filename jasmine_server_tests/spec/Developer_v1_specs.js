// some random tests that ServerV1 should support for Developer
describe("Developer tests for ServerV1 - ", function(){

	var gameApiUrl = "http://1.smg-server.appspot.com/games";
	var settings = '{"accessSignature":"....", "gameId":"....", playerIdOfGameDeveloper:"....", url: "....", name: "...."}';
	var syncMatch = '{"accessSignature":"....", "matchId":"....", playerId:"...."}';
	var parameters = '{"accessSignature":"....", "gameId":"....", playerIdOfGameDeveloper:"....", parameters:"{width:"...", height:"...", minPlayers:"...", maxPlayers:"..."}"}';

	it("Add Game Settings", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("settings")).toBe(true);				
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
			expect(request.response.has("parameters")).toBe(true);				
		};
		request.open("PUT", gameApiUrl, true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(parameters);
	});

	it("Enable pass and play mode", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("passplay")).toBe(true);				
		};
		request.open("GET", gameApiUrl + "?accessSignature=....?gameId:{gameId}?passplay:true", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
	
	it("Set game as perfect information game", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("perfectInfo")).toBe(true);				
		};
		request.open("GET", gameApiUrl + "?accessSignature=....?gameId:{gameId}?perfectInfo:true", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

});