package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by victorlee95 on 8/26/2017.
 */

public class Review {
    UUID ID;
    UUID foreignID_classId;
    UUID reviewerID;
    String opinion;
    String tips;
    int difficulty;
    int usefulness;

}
