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

	// setting of states for searching, error-handling, filters, sorts, and spellchecks
	const [search, setSearch] = useState("");
	const [results, setResults] = useState([]);
	const [searchInfo, setSearchInfo] = useState({});

	const [errorValue, setError] = useState('false');

	const [selectedFieldValue, setSelectedFieldValue] = useState(null);
	const [selectedSortValue, setSelectedSortValue] = useState(null);

	const [filterBy, setFilter] = useState("");
	const [SortBy, setSort] = useState("");

	const [spellCheckValue, setSpellCheck] = useState("");

	
	// function that handles the search query, does a GET request from backend endpoint
	const handleSearch = async e => {
		e.preventDefault();
		if (search === '') return;

		let endpoint = `http://localhost:8080/documents?query=${search}&filter=&sort=`;

		// if sort is set, but filter is not
		if (filterBy==="" && SortBy!=="") {
			endpoint = `http://localhost:8080/documents?query=${search}&filter=&sort=${SortBy}`;
		} // else if sort is not set, and filter is set

		else if (filterBy!=="" && SortBy==="") {
			endpoint = `http://localhost:8080/documents?query=${search}&filter=${filterBy}&sort=`;
		} // else if both sort and filter are set

		else if (filterBy!=="" && SortBy!=="") {
			endpoint = `http://localhost:8080/documents?query=${search}&filter=${filterBy}&sort=${SortBy}`;
		}

		console.log("endpoint is: ", endpoint);
	
		const response = await fetch(endpoint);

		if (!response.ok) {
			setError('true')
			throw Error(response.statusText);
		} else {
			setError('false')
		}

		const json = await response.json();
		console.log(json);

		// if docId of first json is null, backend does spell-checking
		// obtain spellcheck for display to use
		if (json[0].docId === null) {
			let spellcheck = json[0].spellCheck;
			setSpellCheck(spellcheck);
		} else {
			setResults(json);
			setSearchInfo(json);
			setSpellCheck("");
		}
	}

	// if USSlogo is clicked, reset homescreen and reset everything
	const resetSearch = e => {
		setResults([])
		setSearchInfo({})
		setSearch("")
	}

	// handles when user selects field to change
	const handleFieldChange = value => { 

		// if user clicks on 'undo field search', set filter back to empty
		if (value.altKey === false) {
			setSelectedFieldValue("--")
			setFilter("Rating>=1")
		} 
		// else, set field that user has clicked as the filter
		else {
			setSelectedFieldValue(value) 
			setFilter(value.label)
		}
	};


	// handles when user selects sort options
	const handleSortChange = value => {

		// if user clicks on 'undo sorting', set sort back to empty
		if (value.altKey === false) {
			setSelectedSortValue("--") 
			setSort("")

		} // else, set field that user has clicked as the sort
		else {
			setSelectedSortValue(value) 
			setSort(value.label)
		}
	};

	// handles when user clicks on 'read more'
	// this will add the count of that particular document via the docId in the backend and in solr
	const addCount = docId => {
		let doc = docId.toString();
		console.log(doc.type);
		console.log(doc);

		fetch(`http://localhost:8080/documents/count?id=${doc.toString()}`)
		.catch(err=> console.log('ERROR: ', err))

	}

	// returns display
	return (
		<div className="App">
			<header>
				<div>
					<Link to="/">
						<img className='logo' src={logo} alt="" onClick={e => resetSearch()}
						/>
					</Link>
				</div>

				{/* display search bar */}
				<form className="search-box" onSubmit={handleSearch}>
					<input
						type="search"
						placeholder="What are you looking for?"
						value={search}
						onChange={e => setSearch(e.target.value)}
					/>
				</form>

				{/* checks if user spelt words wrongly(spellcheck), if no results are found */}
				{(spellCheckValue !== "") ? <p>Did you mean: "{spellCheckValue} " ? </p> : ''}
				{(Object.keys(searchInfo).length !== 0 && errorValue==='false') ? <p>Search Results: {Object.keys(searchInfo).length}</p> : ''}
				{(errorValue ==='true') ? <p>No search results found! Please refine your search</p> : '' }

				{/*if documents are sent and recevied*/}
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

			{/* display each document on the page */}
			<div className="results">
				{results.map((result, i) => {
					const url = `https://www.tripadvisor.com.sg/Attraction_Review-g294264-d2439664-Reviews-or11280-Universal_Studios_Singapore-Sentosa_Island.html`;
					
					//removing [ and ] for display purposes
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

					let contributionsTemp = result.contributions.replace("[", "");
					let contributions = contributionsTemp.replace("]", "")

					return (
						
						<div className="result" key={i}>
							<h3>"{titleComment}"</h3>
							<p>{commentLike} Upvotes</p>
							<p><b>Rating given:</b> {rating}/5</p>
							<p><b>Date posted</b>: {date}</p>
							<p><b>Reviewer</b>: {user} (<i>{contributions} reviewer contributions</i>)</p>
							<p><b>Country</b>: {country}</p>
							<p><b>Review</b>:</p>

					{/* highlights the matching words that the user queries, in yellow */}
							<i><p dangerouslySetInnerHTML={{ __html: comment.replace(new RegExp(search, "gi"), (match)=> `<mark>${match}</mark>`) }}></p></i>
							<a href={url} onClick={()=> addCount(docId)} target="noreferrer">Read more</a>
						</div>
					)
				})}
			</div>
		</div>
	);
}

export default App;
