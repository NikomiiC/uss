import { useState } from 'react';
import {Link} from 'react-router-dom';
import Select from 'react-select';
import { Container, Row, Col } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import logo from "./logo.png";

const actions = [
	{ label: "DocID", value: 1},
	{ label: "Comment upvotes", value: 2},
	{ label: "Comment Title", value: 3},
	{ label: "Comment content", value: 4},
	{ label: "Comment date", value: 5},
	{ label: "Rating given", value: 6},
	{ label: "Reviewer contribution", value: 7},
	{ label: "Reviewer name", value: 8},
];

function App() {
	const [search, setSearch] = useState("");
	const [results, setResults] = useState([]);
	const [searchInfo, setSearchInfo] = useState({});

	const [inputValue, setValue] = useState("");
	const [selectedValue, setSelectedValue] = useState(null);

	const handleSearch = async e => {
		e.preventDefault();
		if (search === '') return;

		const endpoint = `https://en.wikipedia.org/w/api.php?action=query&list=search&
		prop=info&inprop=url&utf8=&format=json&origin=*&srlimit=10&srsearch=${search}`;

		const response = await fetch(endpoint);

		if (!response.ok) {
			throw Error(response.statusText);
		}

		const json = await response.json();
		console.log(json);

		setResults(json.query.search);
		setSearchInfo(json.query.searchinfo);
	}

	const resetSearch = e => {
		setResults([])
		setSearchInfo({})
		setSearch("")
	}

	//handle filter search
	const handleInputChange = value => {setValue(value)};
	const handleChange = value => {setSelectedValue(value)};

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
				{ (searchInfo.totalhits) ? <p>Search Results: {searchInfo.totalhits}</p> : '' }
				{ (searchInfo.totalhits) ? 
					<div style={{ display: 'flex', width: 750 }}>
						<Row>
							<Col>
								<p>Filter by:</p>
							</Col>
							<Col style={{width:'250px'}}>
								<Select options={ actions } value={selectedValue} onInputChange={handleInputChange}
								onChange={handleChange}/>
							</Col>
							<Col style={{marginTop: 5}}>
								<button onInputChange={handleInputChange} onClick={handleChange}>Unfilter</button>
							</Col>
						</Row>
					</div> : ""
					
				}
			</header>
			<div className="results">
				{results.map((result, i) => {
					console.log("Input value is: ", inputValue);
					console.log("selected value: ", selectedValue);
					console.log("results: ", result);
					const url = `https://en.wikipedia.org/?curid=${result.pageid}`;
					return (
						<div className="result" key={i}>
								<h3>{result.title}</h3>
								<p dangerouslySetInnerHTML={{__html: result.snippet}} ></p>
								<a href={url} target="_blank">Read more</a>
						</div>
					)
				})}
			</div>
		</div>
	);
}

export default App;
