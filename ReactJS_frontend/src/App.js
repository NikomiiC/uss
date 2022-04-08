import { useState } from 'react';
import { Link } from 'react-router-dom';
import Select from 'react-select';
import { Container, Row, Col } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import logo from "./logo.png";

const FieldSearchactions = [
	{ label: "Rating>=1", value: 1 },
	{ label: "Rating>=2", value: 2 },
	{ label: "Rating>=3", value: 3 },
	{ label: "Rating>=4", value: 4 },
	{ label: "Rating>=5", value: 5 },
];

const SortSearchactions = [
	{ label: "Upvotes desc", value: 1 },
	{ label: "Upvotes asc", value: 2 },
	{ label: "Contributions desc", value: 3 },
	{ label: "Contributions asc", value: 4 },
];

function App() {
	const [search, setSearch] = useState("");
	const [results, setResults] = useState([]);
	const [searchInfo, setSearchInfo] = useState({});

	const [errorValue, setError] = useState('false');

	//const [inputValue, setValue] = useState("");
	const [selectedFieldValue, setSelectedFieldValue] = useState(null);
	const [selectedSortValue, setSelectedSortValue] = useState(null);

	const [filterBy, setFilter] = useState("");
	const [SortBy, setSort] = useState("");

	const handleSearch = async e => {
		e.preventDefault();
		if (search === '') return;

		console.log("FILTER BY IS: ", filterBy);
		console.log("SORT BY IS: ", SortBy);
		let endpoint = `http://localhost:8080/documents?query=${search}&filter=&sort=`;

		if (filterBy==="" && SortBy!=="") {
			console.log("FIRST IF IS EXECUTED");
			endpoint = `http://localhost:8080/documents?query=${search}&filter=&sort=${SortBy}`;
		} else if (filterBy!=="" && SortBy==="") {
			console.log("FIRST ELSE-IF IS EXECUTED");
			endpoint = `http://localhost:8080/documents?query=${search}&filter=${filterBy}&sort=`;
		} else if (filterBy!=="" && SortBy!=="") {
			console.log("SECOND ELSE-IF IS EXECUTED");
			endpoint = `http://localhost:8080/documents?query=${search}&filter=${filterBy}&sort=${SortBy}`;
		}

		console.log('ENDPOINT IS: ', endpoint);
		// https://en.wikipedia.org/w/api.php?action=query&list=search&
		// prop=info&inprop=url&utf8=&format=json&origin=*&srlimit=10&srsearch=${search}

		//http://localhost:8080/documents?query=${search}
		const response = await fetch(endpoint);

		if (!response.ok) {
			setError('true')
			throw Error(response.statusText);
		} else {
			setError('false')
		}

		const json = await response.json();
		console.log(json);

		setResults(json);
		setSearchInfo(json);
	}

	const resetSearch = e => {
		setResults([])
		setSearchInfo({})
		setSearch("")
	}

	//handle filter search

	const handleFieldChange = value => { 
		console.log('F VALUE: ', value.altKey);
		if (value.altKey === false) {
			setSelectedFieldValue("--")
			setFilter("")
		} else {
			setSelectedFieldValue(value) 
			setFilter(value.label)
		}
	};
	console.log('F FILTER BY: ', filterBy);
	console.log('F SORT BY: ', SortBy);

	const handleSortChange = value => {
		console.log('S VALUE: ', value.altKey);
		if (value.altKey === false) {
			setSelectedSortValue("--") 
			setSort("")
		} else {
			setSelectedSortValue(value) 
			setSort(value.label)
		}
	};
	console.log('S FILTER BY: ', filterBy);
	console.log('S SORT BY: ', SortBy);
	// const handleFieldSearch = async e => {
	// 	console.log('E is: ', e);
	// 	//e contains {label: ___, value: __}

	// 	const endpoint = `http://localhost:8080/documents?query="${search}"&filter=4&sort=`;

	// 	const response = await fetch(endpoint);

	// 	if (!response.ok) {
	// 		throw Error(response.statusText);
	// 	} 

	// 	const json = await response.json();
	// 	console.log("FIELD SEARCH RESULT IS: ", json);

	// };

	// const handleSortSearch = async e => {
	// 	console.log('E is: ', e);
	// 	//e contains {label: ___, value: __}

	// 	const endpoint = `http://localhost:8080/documents?query="${search}"&filter=4&sort=`;

	// 	const response = await fetch(endpoint);

	// 	if (!response.ok) {
	// 		throw Error(response.statusText);
	// 	} 

	// 	const json = await response.json();
	// 	console.log("SORT SEARCH RESULT IS: ", json);

	// };

	const addCount = value => {
		const requestOptions = {
			method: 'POST',
			headers: { 'Content-Type': 'application/json'},
			body: JSON.stringify(value)
		}
		fetch(`https:localhost:8080/documents/${value}`, requestOptions)
		.then(response => console.log(response.json()))
	}

	return (
		<div className="App">
			<header>
				<div>
					<Link to="/">
						<img className='logo' src={logo} alt="" onClick={e => resetSearch()}
						/>
					</Link>
				</div>
				{/* <h1>USS Advisor</h1> */}
				<form className="search-box" onSubmit={handleSearch}>
					<input
						type="search"
						placeholder="What are you looking for?"
						value={search}
						onChange={e => setSearch(e.target.value)}
					/>
				</form>
				{(Object.keys(searchInfo).length !== 0 && errorValue==='false') ? <p>Search Results: {Object.keys(searchInfo).length}</p> : ''}
				{(errorValue ==='true') ? <p>No search results found! Please refine your search</p> : '' }
				{(Object.keys(searchInfo).length !== 0) ?
					<div style={{ display: 'flex', width: 750 , paddingTop: 15 }}>
						<Row>
							<Col>
								<p>Field Search:</p>
							</Col>
							<Col style={{ width: '250px' }}>
								<Select options={FieldSearchactions} value={selectedFieldValue}
									onChange={handleFieldChange}/>
							</Col>
							<Col style={{ marginTop: 5 }}>
								<button onClick={handleFieldChange}>Undo Field Search</button>
							</Col>
						</Row>

						<Col style={{ width: '250px' }}>
						</Col>
						<Row>
							<Col>
								<p>Sort by:</p>
							</Col>
							<Col style={{ width: '250px' }}>
								<Select options={SortSearchactions} value={selectedSortValue}
									onChange={handleSortChange}/>
							</Col>
							<Col style={{ marginTop: 5 }}>
								<button onClick={handleSortChange}>Undo Sort</button>
							</Col>
						</Row>
					</div> : ""

				}
			</header>
			<div className="results">
				{results.map((result, i) => {
					const url = `https://www.tripadvisor.com.sg/Attraction_Review-g294264-d2439664-Reviews-or11280-Universal_Studios_Singapore-Sentosa_Island.html`;
					
					//removing [ and ] for display
					let titleCommentTemp = result.titleComment.replace("[", "");
					let titleComment = titleCommentTemp.replace("]", "");

					let commentLikeTemp = result.commentLike.replace("[", "");
					let commentLike = commentLikeTemp.replace("]", "");

					let ratingTemp = result.rating.replace("[", "");
					let rating = ratingTemp.replace("]", "");

					let dateTemp = result.date.replace("[", "");
					let date = dateTemp.replace("]", "");

					let userTemp = result.user.replace("[", "");
					let user = userTemp.replace("]", "");

					let countryTemp = result.country.replace("[", "");
					let country = countryTemp.replace("]", "");

					let commentTemp = result.contentComment.replace("[", "");
					let comment = commentTemp.replace("]", "");
					
					let docIdTemp = result.docId.replace("[", "");
					let docId = docIdTemp.replace("]", "")
					
					return (
						
						<div className="result" key={i}>
							<h3>"{titleComment}"</h3>
							<p>{commentLike} Upvotes</p>
							<p><b>Rating given:</b> {rating}/5</p>
							<p><b>Date posted</b>: {date}</p>
							<p><b>Reviewer</b>: {user} </p>
							<p><b>Country</b>: {country}</p>
							<p><b>Review</b>:</p>
							<i><p dangerouslySetInnerHTML={{ __html: comment.replace(new RegExp(search, "gi"), (match)=> `<mark>${match}</mark>`) }}></p></i>
							<button><a href={url} target="noreferrer">Read more</a></button>
						</div>
					)
				})}
			</div>
		</div>
	);
}

export default App;
