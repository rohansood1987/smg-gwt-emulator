// some random tests that ServerV2 should support for Developer
describe("Developer tests for ServerV2 - ", function(){

	var gameApiUrl = "http://2.smg-server.appspot.com/games";
	var gameFaq = "{...}";
	var defaultTime = '{"accessSignature":"....", "gameId":"....","time": "30"}';
	var gameFaq = '{"accessSignature":"....", "gameId":"....","faq": "...."}';


	it("Specify default time per turn", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl + "/timeout", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(defaultTime);
	});
	
	it("Add Game FAQ", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("PUT", gameApiUrl + "/setFaq", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send(gameFaq);
	});

	it("Get statatics of hacker for a game", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("GET", gameApiUrl + "/statatics?hacker=true?accessSignature=....?gameId={gameId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
	
	it("Get statatics of network usage for a game", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("GET", gameApiUrl + "/statatics?network=true?accessSignature=....?gameId={gameId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});

	it("Get game usage statatics", function(){
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			expect(request.readyState).toEqual(4);
			expect(request.status).toEqual(200);
			expect(request.response.has("success")).toBe(true);				
		};
		request.open("GET", gameApiUrl + "/statatics?gameUsage=true?accessSignature=....?gameId={gameId}", true);
		request.responseType = "json";
		request.setRequestHeader("Content-type", "application/json");
		request.send();
	});
});