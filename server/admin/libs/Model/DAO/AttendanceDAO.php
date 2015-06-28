<?php
/** @package Kheldb::Model::DAO */

/** import supporting libraries */
require_once("verysimple/Phreeze/Phreezable.php");
require_once("AttendanceMap.php");

/**
 * AttendanceDAO provides object-oriented access to the attendance table.  This
 * class is automatically generated by ClassBuilder.
 *
 * WARNING: THIS IS AN AUTO-GENERATED FILE
 *
 * This file should generally not be edited by hand except in special circumstances.
 * Add any custom business logic to the Model class which is extended from this DAO class.
 * Leaving this file alone will allow easy re-generation of all DAOs in the event of schema changes
 *
 * @package Kheldb::Model::DAO
 * @author ClassBuilder
 * @version 1.0
 */
class AttendanceDAO extends Phreezable
{
	/** @var int */
	public $Id;

	/** @var date */
	public $HeldOn;

	/** @var int */
	public $LocationId;

	/** @var string */
	public $Coordinators;

	/** @var string */
	public $Modules;

	/** @var string */
	public $Beneficiaries;

	/** @var string */
	public $Comment;

	/** @var int */
	public $RatingSessionObjectives;

	/** @var timestamp */
	public $CreatedAt;

	/** @var timestamp */
	public $ModifiedAt;

	/** @var string */
	public $UserSubmitted;

	/** @var string */
	public $ModeOfTransport;

	/** @var string */
	public $DebriefWhatWorked;

	/** @var string */
	public $DebriefToImprove;

	/** @var string */
	public $DebriefDidntWork;

	/** @var int */
	public $RatingOrgObjectives;

	/** @var int */
	public $RatingFunforkids;


}
?>