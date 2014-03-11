var playerApiUrl = "http://1.smg-server.appspot.com/players"


describe("Player", function() {

	it("Basic test", function() {
		var request = new XMLHttpRequest();
		request.onload = function() {
			expect(request.status).toEqual(200)
		}
		request.open("GET", "http://www.google.com", true)
		request.send();
	})

	it("Add a new player", function() {
		var request = new XMLHttpRequest();
		

	})

})