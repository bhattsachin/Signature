app.controller("ffSearchCtrl", ['$scope','$http' ,function($scope,$http) {
    $scope.movieName = "";
	$scope.movieList = {};
	$scope.movieDetailList = [];

    $scope.getMovies = function (imovieName) {
		console.log("getMovies movie Name");
		if ( imovieName !== undefined && imovieName.length >= 3) {
			console.log(imovieName);
			//$scope._showMovies (imovieName,false);
		} else {
			//$scope._showMovies ();
		}
	};

    $scope._showMovies = function (imovieName,isEmpty) {
		console.log("showing movies");
		var ffShowMovieUrl = "/movie.json"
		if (isEmpty === undefined) {
			ffShowMovieUrl  = '/movieEmpty.json'
		}
		$http({
            method: 'GET',
            url: ffShowMovieUrl,
            data: { 
				movieName : imovieName
			}
			}).success(function (result) {
				console.log(result)
				$scope.movieList = result.movies;
			}).error(function (error) {
                $scope.status = 'Unable to connect' + error.message;
            });
	};

	$scope.addMovie = function(imovieName) {
		console.log("addMovie ")
		var ffDetailMovieUrl = "movieDetails.json"
		$http({
            method: 'GET',
            url: ffDetailMovieUrl ,
            data: { 
				movieName : imovieName
			}
			}).success(function (result) {
				console.log(result)
				$scope.movieDetailList.push(result);
			}).error(function (error) {
                $scope.status = 'Unable to connect' + error.message;
            });

	};




}]);
