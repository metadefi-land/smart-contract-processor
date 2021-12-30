//SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

// import "@openzeppelin/contracts/token/ERC20/ER20.sol";
import "@openzeppelin/contracts/token/ERC721/ERC721.sol";

contract MCP is ERC721 {
	constructor(string memory name, string memory symbol) ERC721(name, symbol) {
        _mint(msg.sender, 1000 * (10 ** 18));
    }
}